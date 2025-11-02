package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "status")
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status")
    private Integer idStatus;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private List<Utilisateur> utilisateurs;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private List<CompteCourant> compteCourants;

    // Constructeurs
    public Status() {
    }

    public Status(String libelle) {
        this.libelle = libelle;
    }

    // Getters et Setters
    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    public List<CompteCourant> getCompteCourants() {
        return compteCourants;
    }

    public void setCompteCourants(List<CompteCourant> compteCourants) {
        this.compteCourants = compteCourants;
    }

    @Override
    public String toString() {
        return "Status{" +
                "idStatus=" + idStatus +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
