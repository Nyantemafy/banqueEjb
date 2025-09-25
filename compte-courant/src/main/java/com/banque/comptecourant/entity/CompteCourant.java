package com.banque.comptecourant.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "comptes_courants")
@NamedQueries({
        @NamedQuery(name = "CompteCourant.findAll", query = "SELECT c FROM CompteCourant c ORDER BY c.dateCreation DESC"),
        @NamedQuery(name = "CompteCourant.findByProprietaire", query = "SELECT c FROM CompteCourant c WHERE c.proprietaire = :proprietaire"),
        @NamedQuery(name = "CompteCourant.countActiveAccounts", query = "SELECT COUNT(c) FROM CompteCourant c WHERE c.actif = true")
})
public class CompteCourant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "numero_compte", length = 20)
    @Size(min = 5, max = 20, message = "Le numéro de compte doit contenir entre 5 et 20 caractères")
    private String numeroCompte;

    @NotNull(message = "Le propriétaire ne peut pas être null")
    @Size(min = 2, max = 100, message = "Le nom du propriétaire doit contenir entre 2 et 100 caractères")
    @Column(name = "proprietaire", nullable = false, length = 100)
    private String proprietaire;

    @NotNull(message = "Le solde ne peut pas être null")
    @Column(name = "solde", nullable = false, precision = 15, scale = 2)
    private BigDecimal solde;

    @Column(name = "date_creation", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @Column(name = "date_derniere_operation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDerniereOperation;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "decouvert_autorise", precision = 10, scale = 2)
    private BigDecimal decouvertAutorise = BigDecimal.ZERO;

    @Size(max = 255)
    @Column(name = "notes", length = 255)
    private String notes;

    @Version
    @Column(name = "version")
    private Long version;

    // Constructeurs
    public CompteCourant() {
        this.solde = BigDecimal.ZERO;
        this.dateCreation = new Date();
        this.actif = true;
    }

    public CompteCourant(String numeroCompte, String proprietaire) {
        this();
        this.numeroCompte = numeroCompte;
        this.proprietaire = proprietaire;
    }

    // Getters et Setters
    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public String getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(String proprietaire) {
        this.proprietaire = proprietaire;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
        this.dateDerniereOperation = new Date();
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateDerniereOperation() {
        return dateDerniereOperation;
    }

    public void setDateDerniereOperation(Date dateDerniereOperation) {
        this.dateDerniereOperation = dateDerniereOperation;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public BigDecimal getDecouvertAutorise() {
        return decouvertAutorise;
    }

    public void setDecouvertAutorise(BigDecimal decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Méthodes utilitaires
    public boolean peutRetirer(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal soldeApresRetrait = this.solde.subtract(montant);
        BigDecimal limiteMinimale = this.decouvertAutorise.negate(); // Le découvert est négatif

        return soldeApresRetrait.compareTo(limiteMinimale) >= 0;
    }

    public boolean estEnDecouvert() {
        return this.solde.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getMontantDecouvert() {
        return estEnDecouvert() ? this.solde.abs() : BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        CompteCourant that = (CompteCourant) obj;
        return numeroCompte != null ? numeroCompte.equals(that.numeroCompte) : that.numeroCompte == null;
    }

    @Override
    public int hashCode() {
        return numeroCompte != null ? numeroCompte.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "numeroCompte='" + numeroCompte + '\'' +
                ", proprietaire='" + proprietaire + '\'' +
                ", solde=" + solde +
                ", actif=" + actif +
                '}';
    }
}