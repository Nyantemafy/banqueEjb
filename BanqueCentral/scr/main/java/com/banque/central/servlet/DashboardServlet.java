package com.banque.central.servlet;

import com.banque.central.util.EJBLocator;
import com.banque.central.model.UserSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.naming.NamingException;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userSession") == null) {
            response.sendRedirect("index.html");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        
        try {
            // Récupérer le compte courant via EJB
            Object compteBean = EJBLocator.lookupCompteBean();
            Method getCompteMethod = compteBean.getClass().getMethod("getCompteByUserId", Integer.class);
            Object compte = getCompteMethod.invoke(compteBean, userSession.getUserId());
            
            if (compte != null) {
                Method getSoldeMethod = compte.getClass().getMethod("getSolde");
                Object solde = getSoldeMethod.invoke(compte);
                request.setAttribute("soldeCourant", solde);
            }

            request.getRequestDispatcher("dashboard.html").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erreur lors du chargement du dashboard");
        }
    }
}
