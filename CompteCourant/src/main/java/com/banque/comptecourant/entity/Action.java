package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "action")
public class Action implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_action")
    private Integer idAction;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    public Action() {}

    public Action(Integer idAction, String libelle) {
        this.idAction = idAction;
        this.libelle = libelle;
    }

    public Integer getIdAction() { return idAction; }
    public void setIdAction(Integer idAction) { this.idAction = idAction; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
