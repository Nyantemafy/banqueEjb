package com.multiplication.ejb;

import com.multiplication.dao.CompteCourantDAORemote;
import com.multiplication.dao.TransactionDAORemote;
import com.multiplication.model.CompteCourant;
import com.multiplication.model.Transaction;
import com.multiplication.model.Type;
import com.multiplication.metier.Virement;
import com.multiplication.metier.VirementComplet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;
import java.util.Date;

@Stateless
public class VirementServiceBean implements VirementService {
    @EJB(lookup = "ejb:/app2-multiplication/CompteCourantDAOApp2!com.multiplication.dao.CompteCourantDAORemote")
    private CompteCourantDAORemote compteCourantDAO;

    @EJB(lookup = "ejb:/app2-multiplication/TransactionDAOApp2!com.multiplication.dao.TransactionDAORemote")
    private TransactionDAORemote transactionDAO;

    /**
     * Effectue un virement avec tous les contrôles
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transaction effectuerVirement(Integer idCompteEmetteur, String compteBeneficiaire,
            String montant, String devise, String date) {
        // Récupérer le compte émetteur
        CompteCourant compteEmet = compteCourantDAO.findById(idCompteEmetteur);
        if (compteEmet == null) {
            throw new IllegalArgumentException("Compte émetteur introuvable");
        }

        // Créer le virement avec les contrôles
        Virement virement = compteEmet.virer(
                idCompteEmetteur.toString(),
                compteBeneficiaire,
                montant,
                devise,
                date);

        // Vérifier les erreurs de contrôles unitaires
        if (virement.hasErreurs()) {
            throw new IllegalArgumentException("Erreurs de validation: " + virement.getErreurs());
        }

        // Contrôle complexe: vérifier le plafond journalier
        BigDecimal montantDejaVire = compteCourantDAO.getMontantVirementsJour(
                idCompteEmetteur, new Date());

        if (!virement.verifierPlafondJournalier(montantDejaVire)) {
            throw new IllegalArgumentException("Erreurs de validation: " + virement.getErreurs());
        }

        // Valider le virement
        virement.valider();

        // Récupérer le compte bénéficiaire
        CompteCourant compteBenef = compteCourantDAO.findById(Integer.parseInt(compteBeneficiaire));
        if (compteBenef == null) {
            throw new IllegalArgumentException("Compte bénéficiaire introuvable");
        }

        // Créer la transaction (EN_ATTENTE). Les mouvements de solde seront effectués à la validation admin
        Type typeVirement = new Type();
        typeVirement.setIdType(3); // VIREMENT

        Transaction transaction = new Transaction();
        transaction.setMontant(virement.getMontant());
        transaction.setCompteCourant(compteEmet);
        transaction.setType(typeVirement);
        transaction.setCompteBeneficiaire(compteBeneficiaire);
        transaction.setDevise(devise);
        transaction.setStatut("EN_ATTENTE");
        transaction.setDateTransaction(virement.getDateVirement());

        // Sauvegarder uniquement la transaction en attente
        transactionDAO.create(transaction);

        return transaction;
    }

    /**
     * Annule un virement avant validation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void annulerVirementAvant(Integer idTransaction) {
        Transaction transaction = transactionDAO.findById(idTransaction);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }

        if (!"EN_ATTENTE".equals(transaction.getStatut())) {
            throw new IllegalStateException("La transaction ne peut être annulée");
        }

        transaction.setStatut("ANNULE");
        transactionDAO.update(transaction);
    }

    /**
     * Annule un virement après validation (virement inverse)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transaction annulerVirementApres(Integer idTransaction) {
        Transaction transactionOriginale = transactionDAO.findById(idTransaction);
        if (transactionOriginale == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }

        if ("ANNULE".equals(transactionOriginale.getStatut())) {
            throw new IllegalStateException("La transaction est déjà annulée");
        }

        // Récupérer les comptes
        CompteCourant compteEmetteur = transactionOriginale.getCompteCourant();
        CompteCourant compteBeneficiaire = compteCourantDAO.findById(
                Integer.parseInt(transactionOriginale.getCompteBeneficiaire()));

        // Faire le virement inverse
        compteBeneficiaire.debiter(transactionOriginale.getMontant());
        compteEmetteur.crediter(transactionOriginale.getMontant());

        // Marquer la transaction originale comme annulée
        transactionOriginale.setStatut("ANNULE");

        // Créer une transaction inverse
        Transaction transactionInverse = new Transaction();
        transactionInverse.setMontant(transactionOriginale.getMontant());
        transactionInverse.setCompteCourant(compteBeneficiaire);
        transactionInverse.setType(transactionOriginale.getType());
        transactionInverse.setCompteBeneficiaire(compteEmetteur.getIdCompteCourant().toString());
        transactionInverse.setDevise(transactionOriginale.getDevise());
        transactionInverse.setStatut("ANNULATION");
        transactionInverse.setReference("ANNULATION_" + idTransaction);

        // Sauvegarder
        compteCourantDAO.update(compteEmetteur);
        compteCourantDAO.update(compteBeneficiaire);
        transactionDAO.update(transactionOriginale);
        transactionDAO.create(transactionInverse);

        return transactionInverse;
    }

    /**
     * Valide un virement en attente (pour admin)
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void validerVirement(Integer idTransaction) {
        Transaction transaction = transactionDAO.findById(idTransaction);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }

        if (!"EN_ATTENTE".equals(transaction.getStatut())) {
            throw new IllegalStateException("La transaction ne peut être validée");
        }

        // Récupérer les comptes pour effectuer les mouvements
        CompteCourant compteEmetteur = transaction.getCompteCourant();
        CompteCourant compteBeneficiaire = compteCourantDAO.findById(
                Integer.parseInt(transaction.getCompteBeneficiaire()));

        // Vérifier solde et effectuer mouvements
        if (!compteEmetteur.debiter(transaction.getMontant())) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        compteBeneficiaire.crediter(transaction.getMontant());

        // Mettre à jour les soldes et statut
        compteCourantDAO.update(compteEmetteur);
        compteCourantDAO.update(compteBeneficiaire);
        transaction.setStatut("VALIDE");
        transactionDAO.update(transaction);
    }
}