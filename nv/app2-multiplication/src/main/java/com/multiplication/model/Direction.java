package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "direction")
public class Direction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direction")
    private Integer idDirection;

    @Column(name = "niveau", nullable = false)
    private Integer niveau;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @OneToMany(mappedBy = "direction", cascade = CascadeType.ALL)
    private List<Utilisateur> utilisateurs;

    // Constructeurs
    public Direction() {
    }

    public Direction(Integer niveau, String libelle) {
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

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
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
