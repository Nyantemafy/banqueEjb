package com.multiplication.ejb;

import com.multiplication.dao.CompteCourantDAORemote;
import com.multiplication.dao.TransactionDAORemote;
import com.multiplication.dao.HistoriqueDAORemote;
import com.multiplication.dao.ActionHistoriqueDAORemote;
import com.multiplication.dao.UtilisateurDAORemote;
import com.multiplication.dao.VirementDAORemote;
import com.multiplication.dao.ConfigurationFraisDAORemote;
import com.multiplication.model.CompteCourant;
import com.multiplication.model.Transaction;
import com.multiplication.model.Type;
import com.multiplication.model.Historique;
import com.multiplication.model.ActionHistorique;
import com.multiplication.model.Utilisateur;
import com.multiplication.model.VirementRef;
import com.multiplication.metier.Virement;
import com.multiplication.metier.VirementComplet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Stateless
public class VirementServiceBean implements VirementService {
    @EJB(lookup = "ejb:/app2-multiplication/CompteCourantDAOApp2!com.multiplication.dao.CompteCourantDAORemote")
    private CompteCourantDAORemote compteCourantDAO;

    @EJB(lookup = "ejb:/app2-multiplication/TransactionDAOApp2!com.multiplication.dao.TransactionDAORemote")
    private TransactionDAORemote transactionDAO;

    @EJB(lookup = "ejb:/app2-multiplication/HistoriqueDAOApp2!com.multiplication.dao.HistoriqueDAORemote")
    private HistoriqueDAORemote historiqueDAO;

    @EJB(lookup = "ejb:/app2-multiplication/ActionHistoriqueDAOApp2!com.multiplication.dao.ActionHistoriqueDAORemote")
    private ActionHistoriqueDAORemote actionHistoriqueDAO;

    @EJB(lookup = "ejb:/app2-multiplication/UtilisateurDAOApp2!com.multiplication.dao.UtilisateurDAORemote")
    private UtilisateurDAORemote utilisateurDAO;

    @EJB(lookup = "ejb:/app2-multiplication/VirementDAOApp2!com.multiplication.dao.VirementDAORemote")
    private VirementDAORemote virementDAO;

    @EJB(lookup = "ejb:/app2-multiplication/ConfigurationFraisDAOApp2!com.multiplication.dao.ConfigurationFraisDAORemote")
    private ConfigurationFraisDAORemote configurationFraisDAO;

    /**
     * Effectue un virement avec tous les contrôles
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Transaction effectuerVirement(Integer idUser, Integer idCompteEmetteur, String compteBeneficiaire,
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

        // Sauvegarder uniquement la transaction en attente (récupérer l'instance persistée avec ID)
        transaction = transactionDAO.create(transaction);

        // Créer la référence virement pour générer le code VIR-XXXXXX
        VirementRef vref = new VirementRef();
        // S'assurer que l'ID transaction est présent
        if (transaction.getIdTransaction() == null) {
            throw new IllegalStateException("ID transaction non généré");
        }
        vref.setTransaction(transaction);
        vref = virementDAO.create(vref);

        // Journaliser l'historique de CREATION (objet = code virement VIR-XXXXXX, frais enregistrés séparément)
        try {
            Utilisateur user = utilisateurDAO.findById(idUser);
            ActionHistorique act = actionHistoriqueDAO.findByIntitule("CREATION");
            Historique h = new Historique();
            String objet = vref != null ? vref.getCodeVirement() : null;
            if (objet == null || objet.isEmpty()) {
                objet = transaction.getIdTransaction() != null
                        ? String.format("VIR-%06d", transaction.getIdTransaction())
                        : "VIREMENT";
            }
            // Calcul frais prévisionnels et enregistrement dans la colonne dédiée
            BigDecimal fraisPrev = configurationFraisDAO.computeFrais("compteCourant", transaction.getDevise(), transaction.getMontant());
            if (fraisPrev != null && fraisPrev.compareTo(BigDecimal.ZERO) > 0) {
                h.setFrais(fraisPrev.setScale(2, RoundingMode.HALF_UP));
            }
            if (objet.length() > 50) objet = objet.substring(0, 50);
            h.setObjet(objet);
            h.setDateHeure(new Date());
            h.setUtilisateur(user);
            h.setActionHistorique(act);
            historiqueDAO.create(h);
        } catch (Exception ignored) {}

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
    public void validerVirement(Integer idUser, Integer idTransaction) {
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

        // Calcul des frais et vérification solde
        BigDecimal frais = configurationFraisDAO.computeFrais("compteCourant", transaction.getDevise(), transaction.getMontant());
        BigDecimal debitTotal = transaction.getMontant().add(frais);
        if (!compteEmetteur.debiter(debitTotal)) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        compteBeneficiaire.crediter(transaction.getMontant());

        // Mettre à jour les soldes et statut
        compteCourantDAO.update(compteEmetteur);
        compteCourantDAO.update(compteBeneficiaire);
        transaction.setStatut("VALIDE");
        transactionDAO.update(transaction);

        // Journaliser l'historique de VALIDATION (objet = code virement VIR-XXXXXX, frais enregistrés séparément)
        try {
            Utilisateur user = utilisateurDAO.findById(idUser);
            ActionHistorique act = actionHistoriqueDAO.findByIntitule("VALIDATION");
            Historique h = new Historique();
            VirementRef vref = virementDAO.findByTransactionId(transaction.getIdTransaction());
            String objet = (vref != null ? vref.getCodeVirement() : String.format("VIR-%06d", transaction.getIdTransaction()));
            if (objet.length() > 50) objet = objet.substring(0, 50);
            h.setObjet(objet);
            if (frais != null) {
                h.setFrais(frais.setScale(2, RoundingMode.HALF_UP));
            }
            h.setDateHeure(new Date());
            h.setUtilisateur(user);
            h.setActionHistorique(act);
            historiqueDAO.create(h);
        } catch (Exception ignored) {}
    }
}