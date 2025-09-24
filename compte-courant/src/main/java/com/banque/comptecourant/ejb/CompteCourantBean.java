package com.banque.comptecourant.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.model.Transaction;

@Stateless
public class CompteCourantBean implements CompteCourantRemote {

    @PersistenceContext(unitName = "compteCourantPU")
    private EntityManager em;

    @Override
    public BigDecimal consulterSolde(String numeroCompte) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, numeroCompte);
            return compte != null ? compte.getSolde() : BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Erreur consultation solde: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean deposer(String numeroCompte, BigDecimal montant, String description) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, numeroCompte);
            if (compte == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // Mise à jour du solde
            compte.setSolde(compte.getSolde().add(montant));

            // Enregistrement de la transaction
            Transaction transaction = new Transaction();
            transaction.setNumeroCompte(numeroCompte);
            transaction.setMontant(montant);
            transaction.setType("DEPOT");
            transaction.setDescription(description);
            transaction.setDateTransaction(new Date());

            em.merge(compte);
            em.persist(transaction);

            return true;
        } catch (Exception e) {
            System.err.println("Erreur dépôt: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean retirer(String numeroCompte, BigDecimal montant, String description) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, numeroCompte);
            if (compte == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // Vérification solde suffisant
            if (compte.getSolde().compareTo(montant) < 0) {
                return false;
            }

            // Mise à jour du solde
            compte.setSolde(compte.getSolde().subtract(montant));

            // Enregistrement de la transaction
            Transaction transaction = new Transaction();
            transaction.setNumeroCompte(numeroCompte);
            transaction.setMontant(montant.negate()); // Montant négatif pour retrait
            transaction.setType("RETRAIT");
            transaction.setDescription(description);
            transaction.setDateTransaction(new Date());

            em.merge(compte);
            em.persist(transaction);

            return true;
        } catch (Exception e) {
            System.err.println("Erreur retrait: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean creerCompte(String numeroCompte, String proprietaire) {
        try {
            if (compteExiste(numeroCompte)) {
                return false;
            }

            CompteCourant compte = new CompteCourant();
            compte.setNumeroCompte(numeroCompte);
            compte.setProprietaire(proprietaire);
            compte.setSolde(BigDecimal.ZERO);
            compte.setDateCreation(new Date());

            em.persist(compte);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur création compte: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean compteExiste(String numeroCompte) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, numeroCompte);
            return compte != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Transaction> getHistoriqueTransactions(String numeroCompte) {
        try {
            return em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.numeroCompte = :numero ORDER BY t.dateTransaction DESC",
                    Transaction.class)
                    .setParameter("numero", numeroCompte)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erreur historique: " + e.getMessage());
            return null;
        }
    }
}
