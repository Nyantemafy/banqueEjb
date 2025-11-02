package com.devises.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Integer idTransaction;

    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_transaction")
    @Temporal(TemporalType.DATE)
    private Date dateTransaction;

    // Références à d'autres entités supprimées côté app1

    @Column(name = "compte_beneficiaire", length = 50)
    private String compteBeneficiaire;

    @Column(name = "devise", length = 10)
    private String devise;

    @Column(name = "statut", length = 20)
    private String statut;

    @Column(name = "reference", length = 100)
    private String reference;

    // Constructeurs
    public Transaction() {
        this.dateTransaction = new Date();
        this.statut = "EN_ATTENTE";
    }

    // Constructeur simplifié sans références à d'autres entités (non présentes dans app1)
    public Transaction(BigDecimal montant) {
        this();
        this.montant = montant;
    }

    // Getters et Setters
    public Integer getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Integer idTransaction) {
        this.idTransaction = idTransaction;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    // Getters/Setters des liens supprimés car entités non présentes côté app1

    public String getCompteBeneficiaire() {
        return compteBeneficiaire;
    }

    public void setCompteBeneficiaire(String compteBeneficiaire) {
        this.compteBeneficiaire = compteBeneficiaire;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", montant=" + montant +
                ", dateTransaction=" + dateTransaction +
                ", statut='" + statut + '\'' +
                '}';
    }
}