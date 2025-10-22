package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Transaction;
import com.banque.comptecourant.entity.Type;
import com.banque.comptecourant.remote.CompteRemote;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
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
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
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

            // Créer la transaction
            Transaction transaction = new Transaction();
            transaction.setIdTransaction(generateTransactionId());
            transaction.setMontant(montant);
            transaction.setDateTransaction(new Date());
            transaction.setCompteCourant(compte);
            
            // Trouver le type "DEPOT"
            Type type = getTypeByLibelle("DEPOT");
            transaction.setType(type);
            
            em.persist(transaction);
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
            transaction.setIdTransaction(generateTransactionId());
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

    private Integer generateTransactionId() {
        TypedQuery<Integer> query = em.createQuery(
            "SELECT MAX(t.idTransaction) FROM Transaction t", Integer.class);
        Integer maxId = query.getSingleResult();
        return (maxId != null ? maxId : 0) + 1;
    }
}
