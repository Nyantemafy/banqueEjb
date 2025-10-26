package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_role")
    private Integer idRole;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    public Role() {}

    public Role(Integer idRole, String libelle) {
        this.idRole = idRole;
        this.libelle = libelle;
    }

    public Integer getIdRole() { return idRole; }
    public void setIdRole(Integer idRole) { this.idRole = idRole; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
