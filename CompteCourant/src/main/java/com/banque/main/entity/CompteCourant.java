package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "compteCourant")
public class CompteCourant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_compteCourant")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCompteCourant;

    @Column(name = "solde", precision = 15, scale = 2)
    private BigDecimal solde;

    @Column(name = "date_ouverture", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOuverture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status")
    private Status status;

    public CompteCourant() {}

    public Integer getIdCompteCourant() { return idCompteCourant; }
    public void setIdCompteCourant(Integer idCompteCourant) { this.idCompteCourant = idCompteCourant; }
    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }
    public Date getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(Date dateOuverture) { this.dateOuverture = dateOuverture; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
