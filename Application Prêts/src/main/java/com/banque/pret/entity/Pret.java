package com.banque.pret.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "prets")
public class Pret implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "numero_pret")
    private String numeroPret;

    @Column(name = "numero_demande")
    private String numeroDemande;

    @Column(name = "numero_client")
    private String numeroClient;

    @Column(name = "montant_initial")
    private BigDecimal montantInitial;

    @Column(name = "montant_restant")
    private BigDecimal montantRestant;

    @Column(name = "taux_interet")
    private Double tauxInteret;

    @Column(name = "duree_en_mois")
    private Integer dureeEnMois;

    @Column(name = "mensualite")
    private BigDecimal mensualite;

    @Column(name = "date_debut")
    private Date dateDebut;

    @Column(name = "statut_pret")
    private String statutPret; // "ACTIF", "REMBOURSE", "SUSPENDU"

    @Column(name = "echeances_payees")
    private Integer echeancesPayees;

    // Constructeurs
    public Pret() {
        this.dateDebut = new Date();
        this.statutPret = "ACTIF";
        this.echeancesPayees = 0;
    }

    public Pret(String numeroPret, String numeroDemande, String numeroClient,
            BigDecimal montant, Double taux, Integer duree, BigDecimal mensualite) {
        this();
        this.numeroPret = numeroPret;
        this.numeroDemande = numeroDemande;
        this.numeroClient = numeroClient;
        this.montantInitial = montant;
        this.montantRestant = montant;
        this.tauxInteret = taux;
        this.dureeEnMois = duree;
        this.mensualite = mensualite;
    }

    // Getters et Setters (tous)
    public String getNumeroPret() {
        return numeroPret;
    }

    public void setNumeroPret(String numeroPret) {
        this.numeroPret = numeroPret;
    }

    public String getNumeroDemande() {
        return numeroDemande;
    }

    public void setNumeroDemande(String numeroDemande) {
        this.numeroDemande = numeroDemande;
    }

    public String getNumeroClient() {
        return numeroClient;
    }

    public void setNumeroClient(String numeroClient) {
        this.numeroClient = numeroClient;
    }

    public BigDecimal getMontantInitial() {
        return montantInitial;
    }

    public void setMontantInitial(BigDecimal montantInitial) {
        this.montantInitial = montantInitial;
    }

    public BigDecimal getMontantRestant() {
        return montantRestant;
    }

    public void setMontantRestant(BigDecimal montantRestant) {
        this.montantRestant = montantRestant;
    }

    public Double getTauxInteret() {
        return tauxInteret;
    }

    public void setTauxInteret(Double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }

    public Integer getDureeEnMois() {
        return dureeEnMois;
    }

    public void setDureeEnMois(Integer dureeEnMois) {
        this.dureeEnMois = dureeEnMois;
    }

    public BigDecimal getMensualite() {
        return mensualite;
    }

    public void setMensualite(BigDecimal mensualite) {
        this.mensualite = mensualite;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getStatutPret() {
        return statutPret;
    }

    public void setStatutPret(String statutPret) {
        this.statutPret = statutPret;
    }

    public Integer getEcheancesPayees() {
        return echeancesPayees;
    }

    public void setEcheancesPayees(Integer echeancesPayees) {
        this.echeancesPayees = echeancesPayees;
    }

    // Méthodes utiles
    public boolean estActif() {
        return "ACTIF".equals(statutPret);
    }

    public boolean estRembourse() {
        return "REMBOURSE".equals(statutPret);
    }

    public int getEcheancesRestantes() {
        return dureeEnMois - (echeancesPayees != null ? echeancesPayees : 0);
    }

    @Override
    public String toString() {
        return "Prêt " + numeroPret + " - " + numeroClient + " - " + montantInitial + "€ - " + statutPret;
    }
}