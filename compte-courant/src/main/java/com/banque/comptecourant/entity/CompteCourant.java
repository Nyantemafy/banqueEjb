package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

// @Entity = Cette classe représente une table dans la base de données
@Entity
@Table(name = "comptes_courants") // Nom de la table
public class CompteCourant {

    // @Id = Clé primaire de la table
    @Id
    @Column(name = "numero_compte")
    private String numeroCompte;

    @Column(name = "proprietaire")
    private String proprietaire;

    @Column(name = "solde")
    private BigDecimal solde;

    @Column(name = "date_creation")
    private Date dateCreation;

    // Constructeur vide (obligatoire pour JPA)
    public CompteCourant() {
        this.solde = BigDecimal.ZERO;
        this.dateCreation = new Date();
    }

    // Constructeur avec paramètres
    public CompteCourant(String numeroCompte, String proprietaire) {
        this();
        this.numeroCompte = numeroCompte;
        this.proprietaire = proprietaire;
    }

    // Getters et Setters (obligatoires)
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
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Compte " + numeroCompte + " - " + proprietaire + " - Solde: " + solde + "€";
    }
}