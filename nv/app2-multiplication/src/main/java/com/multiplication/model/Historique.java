package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "historique")
public class Historique implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_historique", length = 20)
    private String idHistorique; // Généré par trigger côté DB

    @Column(name = "objet", nullable = false, length = 50)
    private String objet; // VIREMENT, DEPOT, RETRAIT

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_heure", nullable = false)
    private Date dateHeure;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_actionhistorique")
    private ActionHistorique actionHistorique;

    public String getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(String idHistorique) {
        this.idHistorique = idHistorique;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public Date getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(Date dateHeure) {
        this.dateHeure = dateHeure;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public ActionHistorique getActionHistorique() {
        return actionHistorique;
    }

    public void setActionHistorique(ActionHistorique actionHistorique) {
        this.actionHistorique = actionHistorique;
    }

    @PrePersist
    protected void onPrePersist() {
        // Génère un identifiant si absent (Hibernate exige un id avant persist pour @Id non généré)
        if (this.idHistorique == null || this.idHistorique.isEmpty()) {
            String prefix = "HIST-";
            String ts = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
            String rnd = String.valueOf((int)(Math.random()*900) + 100); // 3 chiffres
            String id = prefix + ts + rnd; // ex: HIST-241105153045123
            if (id.length() > 20) {
                id = id.substring(0, 20);
            }
            this.idHistorique = id;
        }
        if (this.dateHeure == null) {
            this.dateHeure = new Date();
        }
    }
}
