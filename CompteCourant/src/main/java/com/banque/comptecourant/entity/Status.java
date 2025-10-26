package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "status")
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_status")
    private Integer idStatus;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    public Status() {}

    public Status(Integer idStatus, String libelle) {
        this.idStatus = idStatus;
        this.libelle = libelle;
    }

    public Integer getIdStatus() { return idStatus; }
    public void setIdStatus(Integer idStatus) { this.idStatus = idStatus; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
