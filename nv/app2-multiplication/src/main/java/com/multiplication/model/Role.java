package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Integer idRole;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<ActionRole> actionRoles;

    // Constructeurs
    public Role() {
    }

    public Role(String libelle) {
        this.libelle = libelle;
    }

    // Getters et Setters
    public Integer getIdRole() {
        return idRole;
    }

    public void setIdRole(Integer idRole) {
        this.idRole = idRole;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<ActionRole> getActionRoles() {
        return actionRoles;
    }

    public void setActionRoles(List<ActionRole> actionRoles) {
        this.actionRoles = actionRoles;
    }

    @Override
    public String toString() {
        return "Role{" +
                "idRole=" + idRole +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}