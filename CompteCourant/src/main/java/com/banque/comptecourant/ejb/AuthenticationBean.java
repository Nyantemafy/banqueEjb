package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Utilisateur;
import com.banque.comptecourant.entity.Direction;
import com.banque.comptecourant.entity.Action;
import com.banque.comptecourant.remote.AuthenticationRemote;

import javax.ejb.Stateful;
import javax.ejb.Remove;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;

@Stateful
public class AuthenticationBean implements AuthenticationRemote, Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    private Utilisateur currentUser;
    private Direction[] userDirections;
    private Action[] userActions;
    private boolean authenticated = false;

    @Override
    public Utilisateur authenticate(String username, String password) {
        try {
            TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.username = :username AND u.password = :password", 
                Utilisateur.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            
            currentUser = query.getSingleResult();
            
            if (currentUser != null && currentUser.getStatus().getLibelle().equals("Actif")) {
                authenticated = true;
                
                // Charger les directions
                userDirections = getUserDirections(currentUser.getIdUser());
                
                // Charger les actions
                userActions = getUserActions(currentUser.getIdUser());
                
                return currentUser;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        authenticated = false;
        return null;
    }

    @Override
    public String[] getUserAuthorizedTables(Integer userId) {
        try {
            TypedQuery<String> query = em.createQuery(
                "SELECT DISTINCT ar.nomTable FROM ActionRole ar " +
                "JOIN Utilisateur u ON u.role.idRole = ar.role.idRole " +
                "WHERE u.idUser = :userId",
                String.class);
            query.setParameter("userId", userId);
            return query.getResultList().toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    @Override
    public Direction[] getUserDirections(Integer userId) {
        try {
            TypedQuery<Direction> query = em.createQuery(
                "SELECT d FROM Direction d JOIN Utilisateur u ON u.direction.idDirection = d.idDirection WHERE u.idUser = :userId",
                Direction.class);
            query.setParameter("userId", userId);
            return query.getResultList().toArray(new Direction[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Direction[0];
        }
    }

    @Override
    public Action[] getUserActions(Integer userId) {
        try {
            TypedQuery<Action> query = em.createQuery(
                "SELECT a FROM Action a " +
                "JOIN ActionRole ar ON ar.action.idAction = a.idAction " +
                "JOIN Utilisateur u ON u.role.idRole = ar.role.idRole " +
                "WHERE u.idUser = :userId",
                Action.class);
            query.setParameter("userId", userId);
            return query.getResultList().toArray(new Action[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Action[0];
        }
    }

    @Override
    @Remove
    public void logout() {
        currentUser = null;
        userDirections = null;
        userActions = null;
        authenticated = false;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated && currentUser != null;
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public Direction[] getCurrentUserDirections() {
        return userDirections;
    }

    public Action[] getCurrentUserActions() {
        return userActions;
    }
}
