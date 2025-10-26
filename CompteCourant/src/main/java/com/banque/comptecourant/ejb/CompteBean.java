package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Transaction;
import com.banque.comptecourant.entity.Type;
import com.banque.comptecourant.remote.CompteRemote;
import com.banque.comptecourant.entity.Utilisateur;
import com.banque.comptecourant.entity.Direction;
import com.banque.comptecourant.entity.Status;
import com.banque.comptecourant.entity.Role;

import com.banque.comptecourant.util.ChangeUtil;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless(name = "CompteCourantBean")
public class CompteBean implements CompteRemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public CompteCourant getCompteByUserId(Integer userId) {
        try {
            TypedQuery<CompteCourant> query = em.createQuery(
                    "SELECT c FROM CompteCourant c WHERE c.utilisateur.idUser = :userId",
                    CompteCourant.class);
            query.setParameter("userId", userId);
            query.setMaxResults(1);
            List<CompteCourant> list = query.getResultList();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean depotAvecDate(Integer compteId, BigDecimal montant, String modeDepot, Date date) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            if (compte == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            BigDecimal nouveauSolde = compte.getSolde().add(montant);
            compte.setSolde(nouveauSolde);
            em.merge(compte);
            em.flush();

            Transaction transaction = new Transaction();
            transaction.setMontant(montant);
            transaction.setDateTransaction(date != null ? date : new Date());
            transaction.setCompteCourant(compte);

            Type type = getTypeByLibelle("DEPOT");
            transaction.setType(type);

            em.persist(transaction);
            em.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean retraitAvecDate(Integer compteId, BigDecimal montant, String modeRetrait, Date date) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            if (compte == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            if (compte.getSolde().compareTo(montant) < 0) {
                return false; // Solde insuffisant
            }

            BigDecimal nouveauSolde = compte.getSolde().subtract(montant);
            compte.setSolde(nouveauSolde);
            em.merge(compte);
            em.flush();

            Transaction transaction = new Transaction();
            transaction.setMontant(montant.negate());
            transaction.setDateTransaction(date != null ? date : new Date());
            transaction.setCompteCourant(compte);

            Type type = getTypeByLibelle("RETRAIT");
            transaction.setType(type);

            em.persist(transaction);
            em.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CompteCourant> getAllComptes() {
        TypedQuery<CompteCourant> query = em.createQuery(
                "SELECT c FROM CompteCourant c", CompteCourant.class);
        return query.getResultList();
    }

    @Override
    public BigDecimal getSolde(Integer compteId) {
        CompteCourant compte = em.find(CompteCourant.class, compteId);
        return compte != null ? compte.getSolde() : BigDecimal.ZERO;
    }

    @Override
    public String getEtat(Integer compteId) {
        CompteCourant compte = em.find(CompteCourant.class, compteId);
        if (compte == null || compte.getStatus() == null) {
            return null;
        }
        return compte.getStatus().getLibelle();
    }

    @Override
    public boolean depot(Integer compteId, BigDecimal montant, String modeDepot) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            if (compte == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // Mettre à jour le solde
            BigDecimal nouveauSolde = compte.getSolde().add(montant);
            compte.setSolde(nouveauSolde);
            em.merge(compte);
            em.flush();

            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setMontant(montant);
            transaction.setDateTransaction(new Date());
            transaction.setCompteCourant(compte);

            // Trouver le type "DEPOT"
            Type type = getTypeByLibelle("DEPOT");
            transaction.setType(type);

            em.persist(transaction);
            em.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean retrait(Integer compteId, BigDecimal montant, String modeRetrait) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            if (compte == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // Vérifier le solde
            if (compte.getSolde().compareTo(montant) < 0) {
                return false; // Solde insuffisant
            }

            // Mettre à jour le solde
            BigDecimal nouveauSolde = compte.getSolde().subtract(montant);
            compte.setSolde(nouveauSolde);
            em.merge(compte);

            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setMontant(montant.negate());
            transaction.setDateTransaction(new Date());
            transaction.setCompteCourant(compte);

            // Trouver le type "RETRAIT"
            Type type = getTypeByLibelle("RETRAIT");
            transaction.setType(type);

            em.persist(transaction);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Transaction> getTransactions(Integer compteId) {
        TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :compteId ORDER BY t.dateTransaction DESC",
                Transaction.class);
        query.setParameter("compteId", compteId);
        return query.getResultList();
    }

    @Override
    public List<Transaction> getRecentTransactions(Integer compteId, int limit) {
        TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :compteId ORDER BY t.dateTransaction DESC",
                Transaction.class);
        query.setParameter("compteId", compteId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Integer createUtilisateurEtCompte(String username,
            String password,
            Integer idRole,
            Integer idDirection,
            Integer idStatus,
            BigDecimal soldeInitial) {
        try {
            // Load linked entities
            Role role = em.find(Role.class, idRole);
            Direction direction = em.find(Direction.class, idDirection);
            Status status = em.find(Status.class, idStatus);

            // Persist Utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUsername(username);
            utilisateur.setPassword(password);
            utilisateur.setRole(role);
            utilisateur.setDirection(direction);
            utilisateur.setStatus(status);
            em.persist(utilisateur);

            // Create CompteCourant (ID auto par DB)
            CompteCourant compte = new CompteCourant();
            compte.setUtilisateur(utilisateur);
            compte.setDateOuverture(new Date());
            compte.setStatus(status);
            compte.setSolde(soldeInitial != null ? soldeInitial : BigDecimal.ZERO);
            em.persist(compte);

            // Retourner l'ID généré
            return utilisateur.getIdUser();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Type getTypeByLibelle(String libelle) {
        try {
            TypedQuery<Type> query = em.createQuery(
                    "SELECT t FROM Type t WHERE t.libelle = :libelle", Type.class);
            query.setParameter("libelle", libelle);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Récupère les transactions avec conversion de devise optionnelle
     */
    @Override
    public List<Map<String, Object>> getTransactionsWithCurrency(Integer compteId, String targetCurrency) {
        List<Transaction> transactions = getTransactions(compteId);
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // Tenter d'utiliser Change pour la conversion
            com.banque.change.remote.ChangeRemote changeBean = ChangeUtil.getChangeBean();

            for (Transaction t : transactions) {
                Map<String, Object> row = new HashMap<>();
                row.put("dateTransaction", t.getDateTransaction());

                // Conversion du montant
                if (targetCurrency != null && !targetCurrency.equals("MGA")) {
                    BigDecimal converted = changeBean.convert(
                            t.getMontant(),
                            "MGA",
                            targetCurrency,
                            t.getDateTransaction());
                    row.put("montant", converted);
                    row.put("devise", targetCurrency);
                } else {
                    row.put("montant", t.getMontant());
                    row.put("devise", "MGA");
                }

                result.add(row);
            }
        } catch (Exception e) {
            // Fallback si Change n'est pas disponible
            e.printStackTrace();
            for (Transaction t : transactions) {
                Map<String, Object> row = new HashMap<>();
                row.put("dateTransaction", t.getDateTransaction());
                row.put("montant", t.getMontant());
                row.put("devise", "MGA");
                result.add(row);
            }
        }

        return result;
    }

    /**
     * Récupère la liste des devises disponibles
     */
    @Override
    public List<String> getAvailableCurrencies() {
        try {
            com.banque.change.remote.ChangeRemote changeBean = ChangeUtil.getChangeBean();
            return changeBean.getCurrencies();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback
            return java.util.Arrays.asList("MGA", "EUR", "USD", "KMF", "ZAR");
        }
    }

    /**
     * Récupère la devise par défaut
     */
    @Override
    public String getDefaultCurrency() {
        try {
            com.banque.change.remote.ChangeRemote changeBean = ChangeUtil.getChangeBean();
            return changeBean.getDefaultCurrency();
        } catch (Exception e) {
            return "MGA";
        }
    }

}
