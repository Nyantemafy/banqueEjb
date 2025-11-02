package com.interface_app.servlet;

import com.multiplication.dao.UtilisateurDAORemote;
import com.multiplication.model.Utilisateur;
import com.multiplication.session.SessionInfo;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB(lookup = "ejb:/app2-multiplication/UtilisateurDAOApp2!com.multiplication.dao.UtilisateurDAORemote")
    private UtilisateurDAORemote utilisateurDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Récupérer l'utilisateur
        Utilisateur user = utilisateurDAO.findByUsername(username);

        if (user != null) {
            // Utiliser la méthode d'authentification de l'entité Utilisateur
            // qui crée la SessionInfo Stateful avec toutes les infos
            SessionInfo sessionInfo = user.authentifier(username, password);

            if (sessionInfo != null) {
                // Stocker les informations dans la session HTTP
                HttpSession session = req.getSession();
                session.setAttribute("sessionInfo", sessionInfo);

                // Rediriger selon le rôle (les infos sont dans sessionInfo, pas besoin de
                // requête DB)
                if (sessionInfo.isAdmin()) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                } else if (sessionInfo.isAgent()) {
                    resp.sendRedirect(req.getContextPath() + "/agent/dashboard");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/client/dashboard");
                }
                return;
            }
        }

        // Échec de l'authentification
        req.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}