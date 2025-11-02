package com.devises.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Classe métier représentant une opération de change
 */
public class Change implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idTransaction;
    private BigDecimal montantOriginal;
    private String deviseOriginale;
    private BigDecimal montantConverti;
    private String deviseConverti;
    private BigDecimal tauxChange;
    private Date dateChange;
    private String typeOperation; // "CORRECTION_AVANT" ou "CORRECTION_APRES"
    private String statut;

    public Change() {
        this.dateChange = new Date();
        this.statut = "EN_ATTENTE";
    }

    /**
     * Effectue un change (conversion de devise)
     * 
     * @param montant      montant à convertir
     * @param deviseSource devise source
     * @param deviseCible  devise cible
     * @param taux         taux de change
     * @return objet Change avec les résultats
     */
    public static Change effectuerChange(BigDecimal montant, String deviseSource,
            String deviseCible, BigDecimal taux) {
        Change change = new Change();
        change.montantOriginal = montant;
        change.deviseOriginale = deviseSource;
        change.deviseConverti = deviseCible;
        change.tauxChange = taux;
        change.montantConverti = montant.multiply(taux);
        change.typeOperation = "CHANGE";
        change.statut = "VALIDE";

        return change;
    }

    /**
     * Correction avant transaction: changement de devise uniquement
     * La transaction n'a pas encore été validée
     */
    public static Change correctionAvant(Integer idTransaction, String nouvelleDevise,
            BigDecimal montant, BigDecimal tauxChange) {
        Change change = new Change();
        change.idTransaction = idTransaction;
        change.montantOriginal = montant;
        change.deviseConverti = nouvelleDevise;
        change.tauxChange = tauxChange;
        change.montantConverti = montant.multiply(tauxChange);
        change.typeOperation = "CORRECTION_AVANT";
        change.statut = "VALIDE";

        return change;
    }

    /**
     * Correction après transaction: annulation et nouveau virement
     * La transaction a déjà été validée, il faut l'annuler et en créer une nouvelle
     */
    public static Change correctionApres(Integer idTransactionOrigine,
            BigDecimal montantOriginal,
            String deviseOriginale,
            String nouvelleDevise,
            BigDecimal nouveauTaux) {
        Change change = new Change();
        change.idTransaction = idTransactionOrigine;
        change.montantOriginal = montantOriginal;
        change.deviseOriginale = deviseOriginale;
        change.deviseConverti = nouvelleDevise;
        change.tauxChange = nouveauTaux;
        change.montantConverti = montantOriginal.multiply(nouveauTaux);
        change.typeOperation = "CORRECTION_APRES";
        change.statut = "VALIDE";

        return change;
    }

    // Getters et Setters
    public Integer getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.idTransaction = idTransaction;
    }

    public BigDecimal getMontantOriginal() {
        return montantOriginal;
    }

    public void setMontantOriginal(BigDecimal montantOriginal) {
        this.montantOriginal = montantOriginal;
    }

    public String getDeviseOriginale() {
        return deviseOriginale;
    }

    public void setDeviseOriginale(String deviseOriginale) {
        this.deviseOriginale = deviseOriginale;
    }

    public BigDecimal getMontantConverti() {
        return montantConverti;
    }

    public void setMontantConverti(BigDecimal montantConverti) {
        this.montantConverti = montantConverti;
    }

    public String getDeviseConverti() {
        return deviseConverti;
    }

    public void setDeviseConverti(String deviseConverti) {
        this.deviseConverti = deviseConverti;
    }

    public BigDecimal getTauxChange() {
        return tauxChange;
    }

    public void setTauxChange(BigDecimal tauxChange) {
        this.tauxChange = tauxChange;
    }

    public Date getDateChange() {
        return dateChange;
    }

    public void setDateChange(Date dateChange) {
        this.dateChange = dateChange;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Change{" +
                "idTransaction=" + idTransaction +
                ", montantOriginal=" + montantOriginal +
                ", deviseOriginale='" + deviseOriginale + '\'' +
                ", montantConverti=" + montantConverti +
                ", deviseConverti='" + deviseConverti + '\'' +
                ", tauxChange=" + tauxChange +
                ", typeOperation='" + typeOperation + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
