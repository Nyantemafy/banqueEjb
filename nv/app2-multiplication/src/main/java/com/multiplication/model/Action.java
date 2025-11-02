package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "action")
public class Action implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_action")
    private Integer idAction;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private List<ActionRole> actionRoles;

    // Constructeurs
    public Action() {
    }

    public Action(String libelle) {
        this.libelle = libelle;
    }

    // Getters et Setters
    public Integer getIdAction() {
        return idAction;
    }

    public void setIdAction(Integer idAction) {
        this.idAction = idAction;
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
        return "Action{" +
                "idAction=" + idAction +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}