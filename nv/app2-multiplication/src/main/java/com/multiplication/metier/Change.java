package com.multiplication.metier;

import com.multiplication.model.Transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Métier de change/correction associé à une transaction de virement
 */
public class Change implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idTransactionCible;
    private String ancienneDevise;
    private String nouvelleDevise;
    private BigDecimal taux;
    private BigDecimal montantConverti;
    // Cours (taux de référence) avant et après changement
    private BigDecimal ancienCours;
    private BigDecimal nouveauCours;
    // Semblance (0..1) mesurant la proximité entre ancien et nouveau cours
    private BigDecimal semblance;
    private List<Transaction> operationsLiees;

    public Change() {
        this.operationsLiees = new ArrayList<>();
    }

    public static Change of(Integer idTransaction, String ancienneDevise, String nouvelleDevise, BigDecimal taux) {
        Change c = new Change();
        c.idTransactionCible = idTransaction;
        c.ancienneDevise = ancienneDevise;
        c.nouvelleDevise = nouvelleDevise;
        c.taux = taux;
        c.setCours(taux, taux); // par défaut, même cours → semblance 1
        return c;
    }

    /**
     * Effectue un change simple (retourne un objet métier décrivant l'opération)
     */
    public Change change(String nouvelleDevise, BigDecimal taux) {
        this.nouvelleDevise = nouvelleDevise;
        this.taux = taux;
        // si ancien cours connu, comparer; sinon, utiliser taux comme base
        if (this.ancienCours == null) this.ancienCours = taux;
        this.nouveauCours = taux;
        computeSemblance();
        return this;
    }

    /**
     * Conversion d'un montant selon un taux et une devise cible.
     */
    public Change effectuerChange(BigDecimal montant, String deviseSource, String deviseCible, BigDecimal taux) {
        this.ancienneDevise = deviseSource;
        this.nouvelleDevise = deviseCible;
        this.taux = taux;
        if (montant != null && taux != null) {
            this.montantConverti = montant.multiply(taux);
        }
        // Définir les cours et la semblance
        setCours(taux, taux);
        return this;
    }

    /**
     * Correction avant: on change uniquement la devise d'une transaction non encore validée
     */
    public Change correctionAvant(String nouvelleDevise, BigDecimal taux) {
        return change(nouvelleDevise, taux);
    }

    /**
     * Correction après: on annule l'ancienne transaction et on en crée une nouvelle avec la nouvelle devise
     */
    public Change correctionApres(String nouvelleDevise, BigDecimal taux) {
        return change(nouvelleDevise, taux);
    }

    /**
     * Renseigne la liste des opérations liées (ex: ancienne et nouvelle transaction)
     */
    public Change findOperationLier(List<Transaction> transactions) {
        this.operationsLiees.clear();
        if (transactions != null) this.operationsLiees.addAll(transactions);
        return this;
    }

    public Integer getIdTransactionCible() { return idTransactionCible; }
    public String getAncienneDevise() { return ancienneDevise; }
    public String getNouvelleDevise() { return nouvelleDevise; }
    public BigDecimal getTaux() { return taux; }
    public BigDecimal getMontantConverti() { return montantConverti; }
    public BigDecimal getAncienCours() { return ancienCours; }
    public BigDecimal getNouveauCours() { return nouveauCours; }
    public BigDecimal getSemblance() { return semblance; }
    public List<Transaction> getOperationsLiees() { return new ArrayList<>(operationsLiees); }

    // Helpers
    public Change setCours(BigDecimal ancien, BigDecimal nouveau) {
        this.ancienCours = ancien;
        this.nouveauCours = nouveau;
        computeSemblance();
        return this;
    }

    private void computeSemblance() {
        if (ancienCours == null || nouveauCours == null) {
            this.semblance = BigDecimal.ZERO;
            return;
        }
        BigDecimal max = ancienCours.abs().max(BigDecimal.ONE);
        BigDecimal diff = nouveauCours.subtract(ancienCours).abs();
        BigDecimal ratio = BigDecimal.ONE.subtract(diff.divide(max, 6, java.math.RoundingMode.HALF_UP));
        if (ratio.compareTo(BigDecimal.ZERO) < 0) ratio = BigDecimal.ZERO;
        if (ratio.compareTo(BigDecimal.ONE) > 0) ratio = BigDecimal.ONE;
        this.semblance = ratio;
    }
}
