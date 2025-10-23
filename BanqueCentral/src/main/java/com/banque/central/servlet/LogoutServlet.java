package com.banque.central.servlet;

import com.banque.central.util.EJBLocator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            try {
                // Appeler logout sur l'EJB AuthenticationBean
                Object authBean = EJBLocator.lookupAuthenticationBean();
                Method logoutMethod = authBean.getClass().getMethod("logout");
                logoutMethod.invoke(authBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
