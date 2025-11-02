package com.interface_app.servlet;

import com.multiplication.ejb.AuthenticationServiceBean;
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

    @EJB
    private AuthenticationServiceBean authService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        SessionInfo sessionInfo = authService.login(username, password);

        if (sessionInfo != null) {
            // Stocker les informations dans la session HTTP
            HttpSession session = req.getSession();
            session.setAttribute("sessionInfo", sessionInfo);
            session.setAttribute("authService", authService);

            // Rediriger selon le rôle
            if (sessionInfo.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else if (sessionInfo.isAgent()) {
                resp.sendRedirect(req.getContextPath() + "/agent/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/client/dashboard");
            }
        } else {
            req.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
