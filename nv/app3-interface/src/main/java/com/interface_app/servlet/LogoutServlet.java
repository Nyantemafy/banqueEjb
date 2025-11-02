package com.interface_app.servlet;

import com.multiplication.ejb.AuthenticationServiceBean;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null) {
            // Récupérer le service d'authentification depuis la session
            AuthenticationServiceBean sessionAuthService = (AuthenticationServiceBean) session.getAttribute("authService");

            // Déconnecter l'utilisateur du service Stateful
            if (sessionAuthService != null) {
                sessionAuthService.logout();
            }

            // Invalider la session HTTP
            session.invalidate();
        }

        // Rediriger vers la page de login
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
