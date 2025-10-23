package com.banque.central.servlet;

import com.banque.central.util.EJBLocator;
import com.banque.central.model.UserSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // Lookup de l'EJB AuthenticationBean depuis CompteCourant
            Object authBean = EJBLocator.lookupAuthenticationBean();
            
            // Appel de la méthode authenticate via réflexion
            Method authenticateMethod = authBean.getClass()
                .getMethod("authenticate", String.class, String.class);
            Object utilisateur = authenticateMethod.invoke(authBean, username, password);

            if (utilisateur != null) {
                // Récupérer les informations de l'utilisateur
                Method getIdUserMethod = utilisateur.getClass().getMethod("getIdUser");
                Integer userId = (Integer) getIdUserMethod.invoke(utilisateur);
                
                Method getUsernameMethod = utilisateur.getClass().getMethod("getUsername");
                String userUsername = (String) getUsernameMethod.invoke(utilisateur);

                // Récupérer le rôle (optionnel)
                Integer roleId = null;
                String roleName = null;
                try {
                    Method getRoleMethod = utilisateur.getClass().getMethod("getRole");
                    Object role = getRoleMethod.invoke(utilisateur);
                    if (role != null) {
                        Method getIdRole = role.getClass().getMethod("getIdRole");
                        Method getLibelle = role.getClass().getMethod("getLibelle");
                        roleId = (Integer) getIdRole.invoke(role);
                        roleName = (String) getLibelle.invoke(role);
                    }
                } catch (NoSuchMethodException ignored) {}

                // Récupérer les directions et actions
                Method getDirectionsMethod = authBean.getClass()
                    .getMethod("getUserDirections", Integer.class);
                Object[] directions = (Object[]) getDirectionsMethod.invoke(authBean, userId);

                Method getActionsMethod = authBean.getClass()
                    .getMethod("getUserActions", Integer.class);
                Object[] actions = (Object[]) getActionsMethod.invoke(authBean, userId);

                // Créer la session utilisateur
                UserSession userSession = new UserSession();
                userSession.setUserId(userId);
                userSession.setUsername(userUsername);
                userSession.setAuthenticated(true);

                // Stocker en session HTTP
                HttpSession session = request.getSession(true);
                session.setAttribute("userSession", userSession);
                session.setAttribute("userId", userId);
                session.setAttribute("username", userUsername);
                if (roleId != null) session.setAttribute("roleId", roleId);
                if (roleName != null) session.setAttribute("roleName", roleName);
                session.setAttribute("directions", directions);
                session.setAttribute("actions", actions);
                session.setAttribute("authenticated", true);

                // Redirection vers le dashboard
                response.sendRedirect(request.getContextPath() + "/dashboard.html");
            } else {
                // Échec de l'authentification
                response.sendRedirect(request.getContextPath() + "/index.html?error=invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.html?error=system");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.html");
    }
}