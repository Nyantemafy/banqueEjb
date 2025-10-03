package com.banque.pret.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "demandes_pret")
public class DemandePret implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "numero_demande")
    private String numeroDemande;

    @Column(name = "numero_client")
    private String numeroClient;

    @Column(name = "montant_demande")
    private BigDecimal montantDemande;

    @Column(name = "duree_en_mois")
    private Integer dureeEnMois;

    @Column(name = "objet_pret")
    private String objetPret;

    @Column(name = "statut")
    private String statut; // "EN_ATTENTE", "APPROUVEE", "REJETEE"

    @Column(name = "date_demande")
    private Date dateDemande;

    @Column(name = "date_decision")
    private Date dateDecision;

    @Column(name = "taux_propose")
    private Double tauxPropose;

    @Column(name = "mensualite_calculee")
    private BigDecimal mensualiteCalculee;

    @Column(name = "motif_rejet")
    private String motifRejet;

    // Constructeurs
    public DemandePret() {
        this.dateDemande = new Date();
        this.statut = "EN_ATTENTE";
    }

    public DemandePret(String numeroDemande, String numeroClient, BigDecimal montant,
            Integer duree, String objet) {
        this();
        this.numeroDemande = numeroDemande;
        this.numeroClient = numeroClient;
        this.montantDemande = montant;
        this.dureeEnMois = duree;
        this.objetPret = objet;
    }

    // Getters et Setters (tous)
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

    public BigDecimal getMontantDemande() {
        return montantDemande;
    }

    public void setMontantDemande(BigDecimal montantDemande) {
        this.montantDemande = montantDemande;
    }

    public Integer getDureeEnMois() {
        return dureeEnMois;
    }

    public void setDureeEnMois(Integer dureeEnMois) {
        this.dureeEnMois = dureeEnMois;
    }

    public String getObjetPret() {
        return objetPret;
    }

    public void setObjetPret(String objetPret) {
        this.objetPret = objetPret;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    public Date getDateDecision() {
        return dateDecision;
    }

    public void setDateDecision(Date dateDecision) {
        this.dateDecision = dateDecision;
    }

    public Double getTauxPropose() {
        return tauxPropose;
    }

    public void setTauxPropose(Double tauxPropose) {
        this.tauxPropose = tauxPropose;
    }

    public BigDecimal getMensualiteCalculee() {
        return mensualiteCalculee;
    }

    public void setMensualiteCalculee(BigDecimal mensualiteCalculee) {
        this.mensualiteCalculee = mensualiteCalculee;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    // Méthodes utiles
    public boolean estEnAttente() {
        return "EN_ATTENTE".equals(statut);
    }

    public boolean estApprouvee() {
        return "APPROUVEE".equals(statut);
    }

    public boolean estRejetee() {
        return "REJETEE".equals(statut);
    }

    @Override
    public String toString() {
        return "Demande " + numeroDemande + " - " + numeroClient + " - " + montantDemande + "€ - " + statut;
    }
}