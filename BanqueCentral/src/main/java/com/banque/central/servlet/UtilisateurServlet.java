package com.banque.central.servlet;

import com.banque.central.util.EJBLocator;
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

@WebServlet("/api/utilisateur/create")
public class UtilisateurServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !(Boolean) session.getAttribute("authenticated")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Non authentifié\"}");
            return;
        }

        // Authorization: requires CREATE (or CREER) action and sufficient direction niveau
        Object[] actions = (Object[]) session.getAttribute("actions");
        Object[] directions = (Object[]) session.getAttribute("directions");

        boolean canCreate = false;
        if (actions != null) {
            for (Object a : actions) {
                try {
                    Method getLibelle = a.getClass().getMethod("getLibelle");
                    Object lib = getLibelle.invoke(a);
                    if (lib != null) {
                        String s = lib.toString();
                        if ("CREATE".equalsIgnoreCase(s) || "CREER".equalsIgnoreCase(s)) {
                            canCreate = true;
                            break;
                        }
                    }
                } catch (Exception ignored) { }
            }
        }

        boolean directionOk = false;
        if (directions != null) {
            for (Object d : directions) {
                try {
                    Method getNiveau = d.getClass().getMethod("getNiveau");
                    Object niv = getNiveau.invoke(d);
                    if (niv instanceof Number && ((Number) niv).intValue() >= 1) { // minimal policy; adjust if needed
                        directionOk = true;
                        break;
                    }
                } catch (Exception ignored) { }
            }
        }

        if (!canCreate || !directionOk) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"error\":\"Accès refusé\"}");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String idRoleStr = request.getParameter("id_role"); // optionnel, défaut 2 si absent session
        // id_direction est forcé à 4 (simple user) côté serveur
        String idStatusStr = request.getParameter("id_status"); // optionnel, défaut 1
        String soldeStr = request.getParameter("solde_initial");

        Map<String, Object> result = new HashMap<>();
        try {
            Integer sessionRoleId = null;
            Object sr = session.getAttribute("roleId");
            if (sr instanceof Integer) sessionRoleId = (Integer) sr;
            Integer idRole = idRoleStr != null && !idRoleStr.isEmpty() ? Integer.valueOf(idRoleStr) : (sessionRoleId != null ? sessionRoleId : 2);
            Integer idDirection = 4; // simple user
            Integer idStatus = idStatusStr != null && !idStatusStr.isEmpty() ? Integer.valueOf(idStatusStr) : 1;
            BigDecimal soldeInitial = soldeStr != null && !soldeStr.isEmpty() ? new BigDecimal(soldeStr) : BigDecimal.ZERO;

            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Paramètres requis manquants\"}");
                return;
            }

            Object compteBean = EJBLocator.lookupCompteBean();
            Method createMethod = compteBean.getClass().getMethod(
                    "createUtilisateurEtCompte",
                    String.class, String.class, Integer.class, Integer.class, Integer.class, BigDecimal.class
            );
            Object newUserId = createMethod.invoke(compteBean, username, password, idRole, idDirection, idStatus, soldeInitial);

            if (newUserId instanceof Integer) {
                result.put("success", true);
                result.put("userId", newUserId);
                out.print(gson.toJson(result));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.put("success", false);
                result.put("error", "Création échouée");
                out.print(gson.toJson(result));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("error", e.getMessage());
            out.print(gson.toJson(result));
        }
    }
}
