package com.banque.comptecourant.servlet;

import com.banque.comptecourant.remote.AuthenticationRemote;
import com.banque.comptecourant.entity.Utilisateur;

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
    private static final long serialVersionUID = 1L;

    @EJB
    private AuthenticationRemote authBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Utilisateur user = authBean.authenticate(username, password);

            if (user != null && authBean.isAuthenticated()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getIdUser());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("authenticated", true);

                // Rediriger vers une page de succès
                response.sendRedirect(request.getContextPath() + "/index.jsp?login=success");
            } else {
                request.setAttribute("error", "Identifiants incorrects");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'authentification");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
