package com.banque.comptecourant.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {

    // @GeneratedValue = L'ID s'auto-incrémente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_compte")
    private String numeroCompte;

    @Column(name = "montant")
    private BigDecimal montant;

    @Column(name = "type")
    private String type; // "DEPOT" ou "RETRAIT"

    @Column(name = "date_transaction")
    private Date dateTransaction;

    // Constructeur vide
    public Transaction() {
        this.dateTransaction = new Date();
    }

    // Constructeur simple
    public Transaction(String numeroCompte, BigDecimal montant, String type) {
        this();
        this.numeroCompte = numeroCompte;
        this.montant = montant;
        this.type = type;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    @Override
    public String toString() {
        return type + " de " + montant + "€ le " + dateTransaction;
    }
}