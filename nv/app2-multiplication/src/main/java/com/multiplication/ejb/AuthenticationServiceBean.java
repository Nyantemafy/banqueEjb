package com.multiplication.ejb;

import com.multiplication.dao.UtilisateurDAO;
import com.multiplication.model.Utilisateur;
import com.multiplication.session.SessionInfo;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import java.io.Serializable;

/**
 * Service Stateful pour gérer l'authentification et la session utilisateur
 * La session reste en mémoire tant que l'utilisateur est connecté
 */
@Stateful
public class AuthenticationServiceBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private UtilisateurDAO utilisateurDAO;

    private SessionInfo sessionInfo;
    private boolean isAuthenticated = false;

    /**
     * Authentifie un utilisateur et crée sa session
     */
    public SessionInfo login(String username, String password) {
        Utilisateur user = utilisateurDAO.authentifier(username, password);

        if (user != null) {
            // Utiliser la méthode d'authentification de l'entité Utilisateur
            sessionInfo = user.authentifier(username, password);

            if (sessionInfo != null) {
                isAuthenticated = true;
                return sessionInfo;
            }
        }

        isAuthenticated = false;
        return null;
    }

    /**
     * Déconnecte l'utilisateur
     */
    public void logout() {
        sessionInfo = null;
        isAuthenticated = false;
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        return isAuthenticated && sessionInfo != null;
    }

    /**
     * Obtient les informations de session
     */
    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * Vérifie si l'utilisateur a une permission
     */
    public boolean hasPermission(String action, String table) {
        if (!isAuthenticated || sessionInfo == null) {
            return false;
        }
        return sessionInfo.hasPermission(action, table);
    }

    /**
     * Vérifie si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return isAuthenticated && sessionInfo != null && sessionInfo.isAdmin();
    }

    /**
     * Vérifie si l'utilisateur est agent
     */
    public boolean isAgent() {
        return isAuthenticated && sessionInfo != null && sessionInfo.isAgent();
    }

    /**
     * Vérifie si l'utilisateur est client
     */
    public boolean isClient() {
        return isAuthenticated && sessionInfo != null && sessionInfo.isClient();
    }
}