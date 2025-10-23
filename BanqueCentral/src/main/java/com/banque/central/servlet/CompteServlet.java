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
                // Récupérer les transactions (optionnellement filtrées par date)
                Method getCompteMethod = compteBean.getClass().getMethod("getCompteByUserId", Integer.class);
                Object compte = getCompteMethod.invoke(compteBean, userSession.getUserId());
                
                if (compte != null) {
                    Method getIdMethod = compte.getClass().getMethod("getIdCompteCourant");
                    Integer compteId = (Integer) getIdMethod.invoke(compte);
                    String fromStr = request.getParameter("from");
                    String toStr = request.getParameter("to");

                    Object transactions;
                    if ((fromStr == null || fromStr.isEmpty()) && (toStr == null || toStr.isEmpty())) {
                        Method getRecent = compteBean.getClass().getMethod("getRecentTransactions", Integer.class, int.class);
                        transactions = getRecent.invoke(compteBean, compteId, 10);
                    } else {
                        Method getAll = compteBean.getClass().getMethod("getTransactions", Integer.class);
                        @SuppressWarnings("unchecked")
                        java.util.List<Object> all = (java.util.List<Object>) getAll.invoke(compteBean, compteId);

                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date from = null; java.util.Date to = null;
                        try { if (fromStr != null && !fromStr.isEmpty()) from = sdf.parse(fromStr); } catch (Exception ignore) {}
                        try { if (toStr != null && !toStr.isEmpty()) to = sdf.parse(toStr); } catch (Exception ignore) {}

                        java.util.List<Object> filtered = new java.util.ArrayList<>();
                        for (Object t : all) {
                            try {
                                java.util.Date d = (java.util.Date) t.getClass().getMethod("getDateTransaction").invoke(t);
                                boolean ok = true;
                                if (from != null && d.before(from)) ok = false;
                                if (to != null && d.after(to)) ok = false;
                                if (ok) filtered.add(t);
                            } catch (Exception ignore) {}
                        }
                        transactions = filtered;
                    }

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
                    String montantStr = request.getParameter("montant");
                    if(montantStr==null){ response.setStatus(HttpServletResponse.SC_BAD_REQUEST); out.print("{\"error\":\"Montant manquant\"}"); return; }
                    montantStr = montantStr.trim().replace(" ", "").replace(",", ".");
                    BigDecimal montant;
                    try{ montant = new BigDecimal(montantStr); }catch(Exception ex){ response.setStatus(HttpServletResponse.SC_BAD_REQUEST); out.print("{\"error\":\"Montant invalide\"}"); return; }
                    String mode = request.getParameter("mode");
                    String dateStr = request.getParameter("date");
                    java.util.Date opDate = null;
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try { opDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr); } catch (Exception ignore) {}
                    }

                    boolean success;
                    if (opDate != null) {
                        Method m = compteBean.getClass().getMethod("depotAvecDate", Integer.class, BigDecimal.class, String.class, java.util.Date.class);
                        success = (Boolean) m.invoke(compteBean, compteId, montant, mode, opDate);
                    } else {
                        Method m = compteBean.getClass().getMethod("depot", Integer.class, BigDecimal.class, String.class);
                        success = (Boolean) m.invoke(compteBean, compteId, montant, mode);
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", success);
                    result.put("message", success ? "Dépôt effectué avec succès" : "Échec du dépôt");
                    if (success) {
                        Method getSoldeRemote = compteBean.getClass().getMethod("getSolde", Integer.class);
                        BigDecimal solde = (BigDecimal) getSoldeRemote.invoke(compteBean, compteId);
                        result.put("solde", solde);
                    }
                    out.print(gson.toJson(result));
                    
                } else if ("retrait".equals(action)) {
                    String montantStr = request.getParameter("montant");
                    if(montantStr==null){ response.setStatus(HttpServletResponse.SC_BAD_REQUEST); out.print("{\"error\":\"Montant manquant\"}"); return; }
                    montantStr = montantStr.trim().replace(" ", "").replace(",", ".");
                    BigDecimal montant;
                    try{ montant = new BigDecimal(montantStr); }catch(Exception ex){ response.setStatus(HttpServletResponse.SC_BAD_REQUEST); out.print("{\"error\":\"Montant invalide\"}"); return; }
                    String mode = request.getParameter("mode");
                    String dateStr = request.getParameter("date");
                    java.util.Date opDate = null;
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try { opDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr); } catch (Exception ignore) {}
                    }

                    boolean success;
                    if (opDate != null) {
                        Method m = compteBean.getClass().getMethod("retraitAvecDate", Integer.class, BigDecimal.class, String.class, java.util.Date.class);
                        success = (Boolean) m.invoke(compteBean, compteId, montant, mode, opDate);
                    } else {
                        Method m = compteBean.getClass().getMethod("retrait", Integer.class, BigDecimal.class, String.class);
                        success = (Boolean) m.invoke(compteBean, compteId, montant, mode);
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", success);
                    result.put("message", success ? "Retrait effectué avec succès" : "Échec du retrait");
                    if (success) {
                        Method getSoldeRemote = compteBean.getClass().getMethod("getSolde", Integer.class);
                        BigDecimal solde = (BigDecimal) getSoldeRemote.invoke(compteBean, compteId);
                        result.put("solde", solde);
                    }
                    out.print(gson.toJson(result));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Compte introuvable pour l'utilisateur\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
