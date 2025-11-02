package com.multiplication.ejb;

import com.multiplication.dao.TransactionDAO;
import com.multiplication.metier.Change;
import com.multiplication.model.Transaction;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

@Stateless(name = "ChangeServiceBeanApp2")
public class ChangeServiceBean implements ChangeService {

    @EJB
    private TransactionDAO transactionDAO;

    @EJB
    private VirementService virementService;

    private static final String CHANGE_FILE_PATH = "/opt/jboss/wildfly/standalone/deployments/changes.txt";

    public Change effectuerChange(BigDecimal montant, String deviseSource,
                                  String deviseCible, BigDecimal tauxChange) {
        Change change = new Change().effectuerChange(montant, deviseSource, deviseCible, tauxChange);
        ecrireDansFichier(change);
        return change;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Change correctionAvant(Integer idTransaction, String nouvelleDevise,
                                  BigDecimal tauxChange) {
        Transaction trx = transactionDAO.findById(idTransaction);
        if (trx == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }
        if (!"EN_ATTENTE".equals(trx.getStatut())) {
            throw new IllegalStateException("La transaction doit être en attente");
        }

        Change change = new Change().effectuerChange(trx.getMontant(), trx.getDevise(), nouvelleDevise, tauxChange);

        // Mettre à jour la transaction existante
        trx.setDevise(nouvelleDevise);
        if (change.getMontantConverti() != null) {
            trx.setMontant(change.getMontantConverti());
        }
        transactionDAO.update(trx);

        ecrireDansFichier(change);
        return change;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Change correctionApres(Integer idTransaction, String nouvelleDevise,
                                  BigDecimal tauxChange) {
        Transaction trx = transactionDAO.findById(idTransaction);
        if (trx == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }
        if ("EN_ATTENTE".equals(trx.getStatut())) {
            throw new IllegalStateException("Utiliser correctionAvant pour une transaction en attente");
        }

        Change change = new Change().effectuerChange(trx.getMontant(), trx.getDevise(), nouvelleDevise, tauxChange);

        // Annuler l'ancien virement via service (créera la transaction d'annulation)
        virementService.annulerVirementApres(idTransaction);

        // La création du nouveau virement en nouvelle devise sera déclenchée par l'UI/service métier dédié
        ecrireDansFichier(change);
        return change;
    }

    public List<Transaction> findOperationsLiees(Integer idCompte) {
        return transactionDAO.findByCompte(idCompte);
    }

    private void ecrireDansFichier(Change change) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHANGE_FILE_PATH, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            StringBuilder line = new StringBuilder();
            line.append(change.getIdTransactionCible() != null ? change.getIdTransactionCible() : "N/A");
            line.append(";");
            line.append(change.getAncienneDevise() != null ? change.getAncienneDevise() : "N/A");
            line.append(";");
            line.append(change.getNouvelleDevise() != null ? change.getNouvelleDevise() : "N/A");
            line.append(";");
            line.append(change.getMontantConverti() != null ? change.getMontantConverti() : "N/A");
            line.append(";");
            line.append(change.getTaux() != null ? change.getTaux() : "N/A");
            line.append(";");
            line.append(sdf.format(new java.util.Date()));
            line.append("\n");

            writer.write(line.toString());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier de changes: " + e.getMessage());
        }
    }
}
