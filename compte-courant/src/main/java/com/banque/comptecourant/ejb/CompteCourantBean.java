package com.banque.comptecourant.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.model.Transaction;

// @Stateless = Bean sans état, peut être utilisé par plusieurs clients
@Stateless
public class CompteCourantBean implements CompteCourantRemote {

    // @PersistenceContext = Injection automatique de la connexion base de données
    @PersistenceContext(unitName = "compteCourantPU")
    private EntityManager em; // C'est notre connexion à la base

    @Override
    public BigDecimal consulterSolde(String numeroCompte) {
        System.out.println("🔍 Consultation solde pour : " + numeroCompte);

        // Chercher le compte dans la base
        CompteCourant compte = em.find(CompteCourant.class, numeroCompte);

        if (compte == null) {
            System.out.println("❌ Compte non trouvé");
            return BigDecimal.ZERO;
        }

        System.out.println("✅ Solde trouvé : " + compte.getSolde() + "€");
        return compte.getSolde();
    }

    @Override
    public boolean deposer(String numeroCompte, BigDecimal montant) {
        System.out.println("💰 Dépôt de " + montant + "€ sur " + numeroCompte);

        // Vérifications simples
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("❌ Montant invalide");
            return false;
        }

        // Chercher le compte
        CompteCourant compte = em.find(CompteCourant.class, numeroCompte);
        if (compte == null) {
            System.out.println("❌ Compte non trouvé");
            return false;
        }

        // Ajouter le montant au solde
        BigDecimal nouveauSolde = compte.getSolde().add(montant);
        compte.setSolde(nouveauSolde);

        // Enregistrer la transaction
        Transaction transaction = new Transaction(numeroCompte, montant, "DEPOT");

        // Sauvegarder en base (automatique avec JPA)
        em.merge(compte); // Mettre à jour le compte
        em.persist(transaction); // Créer la transaction

        System.out.println("✅ Dépôt réussi, nouveau solde : " + nouveauSolde + "€");
        return true;
    }

    @Override
    public boolean retirer(String numeroCompte, BigDecimal montant) {
        System.out.println("💸 Retrait de " + montant + "€ sur " + numeroCompte);

        // Vérifications
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("❌ Montant invalide");
            return false;
        }

        CompteCourant compte = em.find(CompteCourant.class, numeroCompte);
        if (compte == null) {
            System.out.println("❌ Compte non trouvé");
            return false;
        }

        // Vérifier si assez d'argent
        if (compte.getSolde().compareTo(montant) < 0) {
            System.out.println("❌ Solde insuffisant : " + compte.getSolde() + "€");
            return false;
        }

        // Retirer l'argent
        BigDecimal nouveauSolde = compte.getSolde().subtract(montant);
        compte.setSolde(nouveauSolde);

        // Enregistrer (montant négatif pour retrait)
        Transaction transaction = new Transaction(numeroCompte, montant.negate(), "RETRAIT");

        em.merge(compte);
        em.persist(transaction);

        System.out.println("✅ Retrait réussi, nouveau solde : " + nouveauSolde + "€");
        return true;
    }

    @Override
    public boolean creerCompte(String numeroCompte, String proprietaire) {
        System.out.println("🆕 Création compte " + numeroCompte + " pour " + proprietaire);

        // Vérifier si le compte existe déjà
        CompteCourant compteExistant = em.find(CompteCourant.class, numeroCompte);
        if (compteExistant != null) {
            System.out.println("❌ Compte déjà existant");
            return false;
        }

        // Créer le nouveau compte
        CompteCourant nouveauCompte = new CompteCourant(numeroCompte, proprietaire);
        em.persist(nouveauCompte); // Sauvegarder en base

        System.out.println("✅ Compte créé avec succès");
        return true;
    }

    @Override
    public List<Transaction> getHistorique(String numeroCompte) {
        System.out.println("📋 Récupération historique pour " + numeroCompte);

        // Requête JPQL simple (comme du SQL mais pour les objets)
        List<Transaction> transactions = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.numeroCompte = :numero ORDER BY t.dateTransaction DESC",
                Transaction.class)
                .setParameter("numero", numeroCompte)
                .getResultList();

        System.out.println("✅ " + transactions.size() + " transactions trouvées");
        return transactions;
    }
}