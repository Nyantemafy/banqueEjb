package com.banque.principale.model;

import java.math.BigDecimal;
import java.util.Date;

public class Client {

    private String numeroClient;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Date dateInscription;

    // Soldes des différents comptes
    private BigDecimal soldeCompteCourant = BigDecimal.ZERO;
    private BigDecimal soldeCompteDepot = BigDecimal.ZERO;
    private BigDecimal totalPrets = BigDecimal.ZERO;

    public Client() {
        this.dateInscription = new Date();
    }

    public Client(String numeroClient, String nom, String prenom) {
        this();
        this.numeroClient = numeroClient;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters et Setters
    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public BigDecimal getSoldeCompteCourant() {
        return soldeCompteCourant;
    }

    public void setSoldeCompteCourant(BigDecimal soldeCompteCourant) {
        this.soldeCompteCourant = soldeCompteCourant;
    }

    public BigDecimal getSoldeCompteDepot() {
        return soldeCompteDepot;
    }

    public void setSoldeCompteDepot(BigDecimal soldeCompteDepot) {
        this.soldeCompteDepot = soldeCompteDepot;
    }

    public BigDecimal getTotalPrets() {
        return totalPrets;
    }

    public void setTotalPrets(BigDecimal totalPrets) {
        this.totalPrets = totalPrets;
    }

    // Méthodes utiles
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public BigDecimal getPatrimoineTotal() {
        return soldeCompteCourant.add(soldeCompteDepot).subtract(totalPrets);
    }

    public String getNumeroCompteCourant() {
        return "CC-" + numeroClient;
    }

    public String getNumeroCompteDepot() {
        return "DEP-" + numeroClient;
    }

    @Override
    public String toString() {
        return String.format("Client %s - %s (%s) - Patrimoine: %s€",
                numeroClient, getNomComplet(), email, getPatrimoineTotal());
    }
}