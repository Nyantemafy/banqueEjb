package com.banque.pret.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "demandes_pret")
@NamedQueries({
        @NamedQuery(name = "DemandePret.findByStatut", query = "SELECT d FROM DemandePret d WHERE d.statut = :statut ORDER BY d.dateDemande DESC"),
        @NamedQuery(name = "DemandePret.findByClient", query = "SELECT d FROM DemandePret d WHERE d.numeroClient = :numeroClient ORDER BY d.dateDemande DESC"),
        @NamedQuery(name = "DemandePret.findEnAttente", query = "SELECT d FROM DemandePret d WHERE d.statut = 'EN_ATTENTE' ORDER BY d.dateDemande ASC"),
        @NamedQuery(name = "DemandePret.countByStatut", query = "SELECT COUNT(d) FROM DemandePret d WHERE d.statut = :statut")
})
public class DemandePret implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "numero_demande", length = 20)
    @Size(min = 5, max = 20)
    private String numeroDemande;

    @NotNull
    @Size(min = 5, max = 20)
    @Column(name = "numero_client", nullable = false, length = 20)
    private String numeroClient;

    @NotNull
    @DecimalMin(value = "1000.00", message = "Le montant minimum est de 1000€")
    @DecimalMax(value = "500000.00", message = "Le montant maximum est de 500000€")
    @Column(name = "montant_demande", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantDemande;

    @NotNull
    @Min(value = 12, message = "La durée minimale est de 12 mois")
    @Max(value = 360, message = "La durée maximale est de 360 mois (30 ans)")
    @Column(name = "duree_en_mois", nullable = false)
    private Integer dureeEnMois;

    @NotNull
    @Size(min = 5, max = 255)
    @Column(name = "objet_pret", nullable = false)
    private String objetPret;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_demande", nullable = false)
    private Date dateDemande;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_decision")
    private Date dateDecision;

    @DecimalMin(value = "0.1")
    @DecimalMax(value = "20.0")
    @Column(name = "taux_propose", precision = 5, scale = 2)
    private Double tauxPropose;

    @Column(name = "mensualite_calculee", precision = 10, scale = 2)
    private BigDecimal mensualiteCalculee;

    @Size(max = 500)
    @Column(name = "motif_decision", length = 500)
    private String motifDecision;

    @Size(max = 100)
    @Column(name = "evaluateur", length = 100)
    private String evaluateur;

    @Column(name = "score_risque")
    private Integer scoreRisque;

    // Informations du client pour l'évaluation
    @Column(name = "revenus_mensuels", precision = 10, scale = 2)
    private BigDecimal revenusMenuels;

    @Column(name = "charges_mensuelles", precision = 10, scale = 2)
    private BigDecimal chargesMenuelles;

    @Size(max = 100)
    @Column(name = "situation_professionnelle", length = 100)
    private String situationProfessionnelle;

    @Column(name = "anciennete_emploi_mois")
    private Integer ancienneteEmploiMois;

    @Column(name = "autres_prets_en_cours", precision = 10, scale = 2)
    private BigDecimal autresPretsEnCours = BigDecimal.ZERO;

    @Size(max = 1000)
    @Column(name = "commentaires", length = 1000)
    private String commentaires;

    @Version
    @Column(name = "version")
    private Long version;

    // Énumération des statuts
    public enum StatutDemande {
        EN_ATTENTE("En attente d'évaluation"),
        EN_COURS_EVALUATION("En cours d'évaluation"),
        APPROUVEE("Approuvée"),
        REJETEE("Rejetée"),
        ANNULEE("Annulée"),
        EXPIREE("Expirée");

        private final String libelle;

        StatutDemande(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    // Constructeurs
    public DemandePret() {
        this.dateDemande = new Date();
        this.statut = StatutDemande.EN_ATTENTE;
        this.autresPretsEnCours = BigDecimal.ZERO;
    }

    public DemandePret(String numeroDemande, String numeroClient,
            BigDecimal montantDemande, Integer dureeEnMois, String objetPret) {
        this();
        this.numeroDemande = numeroDemande;
        this.numeroClient = numeroClient;
        this.montantDemande = montantDemande;
        this.dureeEnMois = dureeEnMois;
        this.objetPret = objetPret;
    }

    // Getters et Setters
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

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    // Méthode de compatibilité pour setStatut(String)
    public void setStatut(String statut) {
        try {
            this.statut = StatutDemande.valueOf(statut);
        } catch (IllegalArgumentException e) {
            this.statut = StatutDemande.EN_ATTENTE;
        }
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

    public String getMotifDecision() {
        return motifDecision;
    }

    public void setMotifDecision(String motifDecision) {
        this.motifDecision = motifDecision;
    }

    public String getEvaluateur() {
        return evaluateur;
    }

    public void setEvaluateur(String evaluateur) {
        this.evaluateur = evaluateur;
    }

    public Integer getScoreRisque() {
        return scoreRisque;
    }

    public void setScoreRisque(Integer scoreRisque) {
        this.scoreRisque = scoreRisque;
    }

    public BigDecimal getRevenusMenuels() {
        return revenusMenuels;
    }

    public void setRevenusMenuels(BigDecimal revenusMenuels) {
        this.revenusMenuels = revenusMenuels;
    }

    public BigDecimal getChargesMenuelles() {
        return chargesMenuelles;
    }

    public void setChargesMenuelles(BigDecimal chargesMenuelles) {
        this.chargesMenuelles = chargesMenuelles;
    }

    public String getSituationProfessionnelle() {
        return situationProfessionnelle;
    }

    public void setSituationProfessionnelle(String situationProfessionnelle) {
        this.situationProfessionnelle = situationProfessionnelle;
    }

    public Integer getAncienneteEmploiMois() {
        return ancienneteEmploiMois;
    }

    public void setAncienneteEmploiMois(Integer ancienneteEmploiMois) {
        this.ancienneteEmploiMois = ancienneteEmploiMois;
    }

    public BigDecimal getAutresPretsEnCours() {
        return autresPretsEnCours;
    }

    public void setAutresPretsEnCours(BigDecimal autresPretsEnCours) {
        this.autresPretsEnCours = autresPretsEnCours;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Méthodes utilitaires
    public boolean peutEtreEvaluee() {
        return statut == StatutDemande.EN_ATTENTE || statut == StatutDemande.EN_COURS_EVALUATION;
    }

    public boolean estValidee() {
        return statut == StatutDemande.APPROUVEE;
    }

    public boolean estRejetee() {
        return statut == StatutDemande.REJETEE;
    }

    public boolean estEnCours() {
        return statut == StatutDemande.EN_ATTENTE || statut == StatutDemande.EN_COURS_EVALUATION;
    }

    public BigDecimal getTauxEndettement() {
        if (revenusMenuels == null || revenusMenuels.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal chargesTotal = chargesMenuelles != null ? chargesMenuelles : BigDecimal.ZERO;
        if (autresPretsEnCours != null) {
            chargesTotal = chargesTotal.add(autresPretsEnCours);
        }
        if (mensualiteCalculee != null) {
            chargesTotal = chargesTotal.add(mensualiteCalculee);
        }

        return chargesTotal.divide(revenusMenuels, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    public BigDecimal getCapaciteRemboursement() {
        if (revenusMenuels == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal charges = chargesMenuelles != null ? chargesMenuelles : BigDecimal.ZERO;
        charges = charges.add(autresPretsEnCours != null ? autresPretsEnCours : BigDecimal.ZERO);

        return revenusMenuels.subtract(charges);
    }

    public int getJoursDepuisDemande() {
        if (dateDemande == null)
            return 0;

        long diffInMillies = new Date().getTime() - dateDemande.getTime();
        return (int) (diffInMillies / (1000 * 60 * 60 * 24));
    }

    public String getStatutLibelle() {
        return statut != null ? statut.getLibelle() : "Inconnu";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        DemandePret that = (DemandePret) obj;
        return numeroDemande != null ? numeroDemande.equals(that.numeroDemande) : that.numeroDemande == null;
    }

    @Override
    public int hashCode() {
        return numeroDemande != null ? numeroDemande.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DemandePret{" +
                "numeroDemande='" + numeroDemande + '\'' +
                ", numeroClient='" + numeroClient + '\'' +
                ", montantDemande=" + montantDemande +
                ", statut=" + statut +
                ", dateDemande=" + dateDemande +
                '}';
    }
}