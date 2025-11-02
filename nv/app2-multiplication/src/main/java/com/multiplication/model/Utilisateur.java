package com.multiplication.model;

import com.multiplication.session.SessionInfo;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "utilisateur")
public class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_actionRole")
    private ActionRole actionRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_direction")
    private Direction direction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status")
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    private Role role;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<CompteCourant> compteCourants;

    // Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Authentifie l'utilisateur et crée une session Stateful
     * Toutes les informations nécessaires sont chargées une seule fois
     * 
     * @param usernameInput username saisi
     * @param passwordInput mot de passe saisi
     * @return SessionInfo contenant les informations de session ou null si échec
     */
    public SessionInfo authentifier(String usernameInput, String passwordInput) {
        // Vérification des credentials
        if (!this.username.equals(usernameInput) || !this.password.equals(passwordInput)) {
            return null;
        }

        // Vérification du statut
        if (this.status == null || !"Actif".equals(this.status.getLibelle())) {
            return null;
        }

        // Création de la session avec TOUTES les informations
        SessionInfo session = new SessionInfo();
        session.setIdUser(this.idUser);
        session.setUsername(this.username);
        
        // Charger le rôle principal depuis id_role si présent, sinon depuis actionRole
        session.setRole(this.role != null ? this.role : (this.actionRole != null ? this.actionRole.getRole() : null));

        // Charger TOUS les ActionRole de l'utilisateur
        List<ActionRole> actionRoles = new ArrayList<>();
        if (this.actionRole != null) {
            actionRoles.add(this.actionRole);
            // Si l'utilisateur a plusieurs ActionRole, les charger ici
            // Pour l'instant on suppose qu'il n'en a qu'un
        }
        session.setActionRoles(actionRoles);

        // Charger TOUTES les Directions
        List<Direction> directions = new ArrayList<>();
        if (this.direction != null) {
            directions.add(this.direction);
        }
        session.setDirections(directions);

        // Charger le statut
        session.setStatus(this.status);

        return session;
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     */
    public boolean hasPermission(String actionLibelle, String tableName) {
        if (this.actionRole == null) {
            return false;
        }

        return this.actionRole.getAction().getLibelle().equals(actionLibelle)
                && this.actionRole.getNomTable().equals(tableName);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ActionRole getActionRole() {
        return actionRole;
    }

    public void setActionRole(ActionRole actionRole) {
        this.actionRole = actionRole;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<CompteCourant> getCompteCourants() {
        return compteCourants;
    }

    public void setCompteCourants(List<CompteCourant> compteCourants) {
        this.compteCourants = compteCourants;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", status=" + (status != null ? status.getLibelle() : "null") +
                ", role=" + (actionRole != null && actionRole.getRole() != null ? 
                    actionRole.getRole().getLibelle() : "null") +
                '}';
    }
}