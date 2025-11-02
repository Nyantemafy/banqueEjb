package com.devises.ejb;

import com.devises.dao.TransactionDAO;
import com.devises.model.Transaction;
import com.devises.model.Change;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Stateless
public class ChangeServiceBean {

    @EJB
    private TransactionDAO transactionDAO;

    private static final String CHANGE_FILE_PATH = "/opt/jboss/wildfly/standalone/deployments/changes.txt";

    /**
     * Effectue un change et écrit dans le fichier
     */
    public Change effectuerChange(BigDecimal montant, String deviseSource, 
                                  String deviseCible, BigDecimal tauxChange) {
        Change change = Change.effectuerChange(montant, deviseSource, deviseCible, tauxChange);
        
        // Écrire dans le fichier
        ecrireDansFichier(change);
        
        return change;
    }

    /**
     * Correction avant validation: changement de devise seulement
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Change correctionAvant(Integer idTransaction, String nouvelleDevise, 
                                  BigDecimal tauxChange) {
        Transaction transaction = transactionDAO.findById(idTransaction);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }

        if (!"EN_ATTENTE".equals(transaction.getStatut())) {
            throw new IllegalStateException("La transaction doit être en attente");
        }

        // Créer l'objet Change
        Change change = Change.correctionAvant(
            idTransaction, 
            nouvelleDevise, 
            transaction.getMontant(),
            tauxChange
        );

        // Mettre à jour la transaction
        transaction.setDevise(nouvelleDevise);
        transaction.setMontant(change.getMontantConverti());
        transactionDAO.update(transaction);

        // Écrire dans le fichier
        ecrireDansFichier(change);

        return change;
    }

    /**
     * Correction après validation: annulation et nouveau virement
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Change correctionApres(Integer idTransaction, String nouvelleDevise, 
                                  BigDecimal tauxChange) {
        Transaction transaction = transactionDAO.findById(idTransaction);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction introuvable");
        }

        if ("EN_ATTENTE".equals(transaction.getStatut())) {
            throw new IllegalStateException("Utiliser correctionAvant pour une transaction en attente");
        }

        // Créer l'objet Change
        Change change = Change.correctionApres(
            idTransaction,
            transaction.getMontant(),
            transaction.getDevise(),
            nouvelleDevise,
            tauxChange
        );

        // Créer un nouveau virement avec la nouvelle devise
        // Note: Ce serait fait via l'interface utilisateur normalement
        
        // Écrire dans le fichier
        ecrireDansFichier(change);

        return change;
    }

    /**
     * Écrit les informations de change dans le fichier texte
     */
    private void ecrireDansFichier(Change change) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHANGE_FILE_PATH, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            StringBuilder line = new StringBuilder();
            line.append(change.getIdTransaction() != null ? change.getIdTransaction() : "N/A");
            line.append(";");
            line.append(change.getMontantOriginal());
            line.append(";");
            line.append(change.getDeviseOriginale() != null ? change.getDeviseOriginale() : "N/A");
            line.append(";");
            line.append(change.getMontantConverti());
            line.append(";");
            line.append(change.getDeviseConverti());
            line.append(";");
            line.append(change.getTauxChange());
            line.append(";");
            line.append(sdf.format(change.getDateChange()));
            line.append(";");
            line.append(change.getTypeOperation());
            line.append(";");
            line.append(change.getStatut());
            line.append("\n");
            
            writer.write(line.toString());
            writer.flush();
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture dans le fichier de changes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}