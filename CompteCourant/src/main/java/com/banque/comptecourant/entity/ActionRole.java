package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "actionRole")
public class ActionRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_actionRole")
    private Integer idActionRole;

    @Column(name = "nomTable", nullable = false, length = 50)
    private String nomTable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_action")
    private Action action;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    private Role role;

    public ActionRole() {}

    public Integer getIdActionRole() { return idActionRole; }
    public void setIdActionRole(Integer idActionRole) { this.idActionRole = idActionRole; }
    public String getNomTable() { return nomTable; }
    public void setNomTable(String nomTable) { this.nomTable = nomTable; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
