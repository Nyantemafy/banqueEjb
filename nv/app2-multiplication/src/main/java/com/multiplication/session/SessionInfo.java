package com.multiplication.session;

import com.multiplication.model.ActionRole;
import com.multiplication.model.Direction;
import com.multiplication.model.Role;
import com.multiplication.model.Status;
import java.io.Serializable;
import java.util.List;

/**
 * Classe Stateful contenant les informations de session utilisateur
 * Ces informations sont chargées UNE SEULE FOIS lors de l'authentification
 * et sont ensuite disponibles en mémoire sans requête à la base de données
 * 
 * Cela évite de faire des requêtes répétées à chaque vérification de permission
 */
public class SessionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idUser;
    private String username;
    private Role role;
    private List<ActionRole> actionRoles; // TOUS les ActionRole de l'utilisateur
    private List<Direction> directions; // TOUTES les Directions de l'utilisateur
    private Status status;
    private Long loginTimestamp;

    public SessionInfo() {
        this.loginTimestamp = System.currentTimeMillis();
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     * AUCUNE requête DB nécessaire, tout est en mémoire
     */
    public boolean hasPermission(String actionLibelle, String tableName) {
        if (actionRoles == null || actionRoles.isEmpty()) {
            return false;
        }

        for (ActionRole ar : actionRoles) {
            if (ar.getAction().getLibelle().equals(actionLibelle)
                    && ar.getNomTable().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur est Admin
     * AUCUNE requête DB nécessaire
     */
    public boolean isAdmin() {
        return role != null && "ADMIN".equals(role.getLibelle());
    }

    /**
     * Vérifie si l'utilisateur est Agent
     * AUCUNE requête DB nécessaire
     */
    public boolean isAgent() {
        return role != null && "AGENT".equals(role.getLibelle());
    }

    /**
     * Vérifie si l'utilisateur est Agent supérieur
     * AUCUNE requête DB nécessaire
     */
    public boolean isAgentSup() {
        return role != null && "AGENT_SUP".equals(role.getLibelle());
    }

    /**
     * Vérifie si l'utilisateur est Client
     * AUCUNE requête DB nécessaire
     */
    public boolean isClient() {
        return role != null && "CLIENT".equals(role.getLibelle());
    }

    /**
     * Obtient le niveau de direction de l'utilisateur
     * AUCUNE requête DB nécessaire
     */
    public Integer getNiveauDirection() {
        if (directions != null && !directions.isEmpty()) {
            return directions.get(0).getNiveau();
        }
        return null;
    }

    // Getters et Setters
    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<ActionRole> getActionRoles() {
        return actionRoles;
    }

    public void setActionRoles(List<ActionRole> actionRoles) {
        this.actionRoles = actionRoles;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(Long loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", role=" + (role != null ? role.getLibelle() : "null") +
                ", niveauDirection=" + getNiveauDirection() +
                ", status=" + (status != null ? status.getLibelle() : "null") +
                ", loginTimestamp=" + loginTimestamp +
                '}';
    }
}