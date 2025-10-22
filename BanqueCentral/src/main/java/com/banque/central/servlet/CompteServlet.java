package com.banque.central.servlet;

import com.banque.central.util.EJBLocator;
import com.banque.central.model.UserSession;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/compte/*")
public class CompteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Non authentifié\"}");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        String pathInfo = request.getPathInfo();

        try {
            Object compteBean = EJBLocator.lookupCompteBean();
            
            if ("/solde".equals(pathInfo)) {
                // Récupérer le solde depuis l'EJB (méthode getSolde)
                Method getCompteMethod = compteBean.getClass().getMethod("getCompteByUserId", Integer.class);
                Object compte = getCompteMethod.invoke(compteBean, userSession.getUserId());
                
                if (compte != null) {
                    Method getIdMethod = compte.getClass().getMethod("getIdCompteCourant");
                    Integer compteId = (Integer) getIdMethod.invoke(compte);
                    Method getSoldeRemote = compteBean.getClass().getMethod("getSolde", Integer.class);
                    BigDecimal solde = (BigDecimal) getSoldeRemote.invoke(compteBean, compteId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("solde", solde);
                    out.print(gson.toJson(result));
                }
            } else if ("/etat".equals(pathInfo)) {
                // Récupérer l'état du compte depuis l'EJB (méthode getEtat)
                Method getCompteMethod = compteBean.getClass().getMethod("getCompteByUserId", Integer.class);
                Object compte = getCompteMethod.invoke(compteBean, userSession.getUserId());
                if (compte != null) {
                    Method getIdMethod = compte.getClass().getMethod("getIdCompteCourant");
                    Integer compteId = (Integer) getIdMethod.invoke(compte);
                    Method getEtatRemote = compteBean.getClass().getMethod("getEtat", Integer.class);
                    String etat = (String) getEtatRemote.invoke(compteBean, compteId);
                    Map<String, Object> result = new HashMap<>();
                    result.put("etat", etat);
                    out.print(gson.toJson(result));
                }
            } else if ("/transactions".equals(pathInfo)) {
                // Récupérer les transactions
                Method getCompteMethod = compteBean.getClass().getMethod("getCompteByUserId", Integer.class);
                Object compte = getCompteMethod.invoke(compteBean, userSession.getUserId());
                
                if (compte != null) {
                    Method getIdMethod = compte.getClass().getMethod("getIdCompteCourant");
                    Integer compteId = (Integer) getIdMethod.invoke(compte);
                    
                    Method getTransactionsMethod = compteBean.getClass()
                        .getMethod("getRecentTransactions", Integer.class, int.class);
                    Object transactions = getTransactionsMethod.invoke(compteBean, compteId, 10);
                    
                    out.print(gson.toJson(transactions));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userSession") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Non authentifié\"}");
            return;
        }

        UserSession userSession = (UserSession) session.getAttribute("userSession");
        String action = request.getParameter("action");

        try {
            Object compteBean = EJBLocator.lookupCompteBean();
            
            // Récupérer le compte
            Method getCompteMethod = compteBean.getClass().getMethod("getCompteByUserId", Integer.class);
            Object compte = getCompteMethod.invoke(compteBean, userSession.getUserId());
            
            if (compte != null) {
                Method getIdMethod = compte.getClass().getMethod("getIdCompteCourant");
                Integer compteId = (Integer) getIdMethod.invoke(compte);
                
                if ("depot".equals(action)) {
                    BigDecimal montant = new BigDecimal(request.getParameter("montant"));
                    String mode = request.getParameter("mode");
                    
                    Method depotMethod = compteBean.getClass()
                        .getMethod("depot", Integer.class, BigDecimal.class, String.class);
                    boolean success = (Boolean) depotMethod.invoke(compteBean, compteId, montant, mode);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", success);
                    result.put("message", success ? "Dépôt effectué avec succès" : "Échec du dépôt");
                    out.print(gson.toJson(result));
                    
                } else if ("retrait".equals(action)) {
                    BigDecimal montant = new BigDecimal(request.getParameter("montant"));
                    String mode = request.getParameter("mode");
                    
                    Method retraitMethod = compteBean.getClass()
                        .getMethod("retrait", Integer.class, BigDecimal.class, String.class);
                    boolean success = (Boolean) retraitMethod.invoke(compteBean, compteId, montant, mode);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", success);
                    result.put("message", success ? "Retrait effectué avec succès" : "Échec du retrait");
                    out.print(gson.toJson(result));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
