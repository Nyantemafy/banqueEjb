// ==================== AuthenticationBean.java ====================
package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Utilisateur;
import com.banque.comptecourant.entity.Direction;
import com.banque.comptecourant.entity.Action;
import com.banque.comptecourant.entity.ActionRole;
import com.banque.comptecourant.remote.AuthenticationRemote;

import javax.ejb.Stateful;
import javax.ejb.Remove;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.username = :username AND u.password = :password",
                Utilisateur.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<Utilisateur> result = query.getResultList();
        if (result.isEmpty()) {
            authenticated = false;
            currentUser = null;
            userDirections = null;
            userActions = null;
            return null;
        }
        currentUser = result.get(0);
        authenticated = true;
        // Directions: based on schema, user has a single Direction; we expose as array
        List<Direction> dirs = new ArrayList<>();
        if (currentUser.getDirection() != null) {
            dirs.add(currentUser.getDirection());
        }
        userDirections = dirs.toArray(new Direction[0]);
        // Actions: from user's ActionRole mapping
        List<Action> acts = new ArrayList<>();
        if (currentUser.getActionRole() != null && currentUser.getActionRole().getAction() != null) {
            acts.add(currentUser.getActionRole().getAction());
        } else {
            // Optionally, load additional actions by role if needed
            if (currentUser.getActionRole() != null && currentUser.getActionRole().getRole() != null) {
                TypedQuery<ActionRole> arq = em.createQuery(
                        "SELECT ar FROM ActionRole ar WHERE ar.role.idRole = :rid", ActionRole.class);
                arq.setParameter("rid", currentUser.getActionRole().getRole().getIdRole());
                for (ActionRole ar : arq.getResultList()) {
                    if (ar.getAction() != null) acts.add(ar.getAction());
                }
            }
        }
        userActions = acts.toArray(new Action[0]);
        return currentUser;
    }

    @Override
    public Direction[] getUserDirections(Integer userId) {
        if (!authenticated || currentUser == null || !currentUser.getIdUser().equals(userId)) {
            return new Direction[0];
        }
        return userDirections != null ? userDirections : new Direction[0];
    }

    @Override
    public Action[] getUserActions(Integer userId) {
        if (!authenticated || currentUser == null || !currentUser.getIdUser().equals(userId)) {
            return new Action[0];
        }
        return userActions != null ? userActions : new Action[0];
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    @Remove
    public void logout() {
        authenticated = false;
        currentUser = null;
        userDirections = null;
        userActions = null;
    }
}