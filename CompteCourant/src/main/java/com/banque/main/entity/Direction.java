package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "direction")
public class Direction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_direction")
    private Integer idDirection;

    @Column(name = "niveau", nullable = false)
    private Integer niveau;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    // Constructeurs
    public Direction() {
    }

    public Direction(Integer idDirection, Integer niveau, String libelle) {
        this.idDirection = idDirection;
        this.niveau = niveau;
        this.libelle = libelle;
    }

    // Getters et Setters
    public Integer getIdDirection() {
        return idDirection;
    }

    public void setIdDirection(Integer idDirection) {
        this.idDirection = idDirection;
    }

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return "Direction{" +
                "idDirection=" + idDirection +
                ", niveau=" + niveau +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}