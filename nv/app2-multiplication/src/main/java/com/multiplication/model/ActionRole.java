package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "actionRole")
public class ActionRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actionRole")
    private Integer idActionRole;

    @Column(name = "nomTable", nullable = false, length = 50)
    private String nomTable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_action", nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "actionRole", cascade = CascadeType.ALL)
    private List<Utilisateur> utilisateurs;

    // Constructeurs
    public ActionRole() {
    }

    public ActionRole(String nomTable, Action action, Role role) {
        this.nomTable = nomTable;
        this.action = action;
        this.role = role;
    }

    // Getters et Setters
    public Integer getIdActionRole() {
        return idActionRole;
    }

    public void setIdActionRole(Integer idActionRole) {
        this.idActionRole = idActionRole;
    }

    public String getNomTable() {
        return nomTable;
    }

    public void setNomTable(String nomTable) {
        this.nomTable = nomTable;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }

    @Override
    public String toString() {
        return "ActionRole{" +
                "idActionRole=" + idActionRole +
                ", nomTable='" + nomTable + '\'' +
                ", action=" + action +
                ", role=" + role +
                '}';
    }
}
