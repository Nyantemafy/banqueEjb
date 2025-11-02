package com.multiplication.ejb;

import com.multiplication.dao.UtilisateurDAORemote;
import com.multiplication.model.Utilisateur;
import com.multiplication.session.SessionInfo;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Remote;
import java.io.Serializable;

/**
 * Service Stateful pour gérer l'authentification et la session utilisateur
 * La session reste en mémoire tant que l'utilisateur est connecté
 */
@Stateful
@Remote(AuthenticationService.class)
public class AuthenticationServiceBean implements AuthenticationService, Serializable {
    private static final long serialVersionUID = 1L;

    @EJB(lookup = "ejb:/app2-multiplication/UtilisateurDAOApp2!com.multiplication.dao.UtilisateurDAORemote")
    private UtilisateurDAORemote utilisateurDAO;

    private SessionInfo sessionInfo;
    private boolean isAuthenticated = false;

    /**
     * Authentifie un utilisateur et crée sa session
     */
    @Override
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
    @Override
    public void logout() {
        sessionInfo = null;
        isAuthenticated = false;
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     */
    @Override
    public boolean isAuthenticated() {
        return isAuthenticated && sessionInfo != null;
    }

    /**
     * Obtient les informations de session
     */
    @Override
    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * Vérifie si l'utilisateur a une permission
     */
    @Override
    public boolean hasPermission(String action, String table) {
        if (!isAuthenticated || sessionInfo == null) {
            return false;
        }
        return sessionInfo.hasPermission(action, table);
    }

    /**
     * Vérifie si l'utilisateur est admin
     */
    @Override
    public boolean isAdmin() {
        return isAuthenticated && sessionInfo != null && sessionInfo.isAdmin();
    }

    /**
     * Vérifie si l'utilisateur est agent
     */
    @Override
    public boolean isAgent() {
        return isAuthenticated && sessionInfo != null && sessionInfo.isAgent();
    }

    /**
     * Vérifie si l'utilisateur est client
     */
    @Override
    public boolean isClient() {
        return isAuthenticated && sessionInfo != null && sessionInfo.isClient();
    }
}