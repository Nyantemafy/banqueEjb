package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "type")
public class Type implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_type")
    private Integer idType;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    public Type() {}

    public Type(Integer idType, String libelle) {
        this.idType = idType;
        this.libelle = libelle;
    }

    public Integer getIdType() { return idType; }
    public void setIdType(Integer idType) { this.idType = idType; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
