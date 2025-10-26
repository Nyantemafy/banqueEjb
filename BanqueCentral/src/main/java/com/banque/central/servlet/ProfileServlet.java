package com.banque.central.servlet;

import com.google.gson.Gson;
import com.banque.central.util.EJBLocator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet("/api/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null || !(Boolean) session.getAttribute("authenticated")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Non authentifié\"}");
            return;
        }

        Map<String, Object> result = new HashMap<>();
        try {
            result.put("userId", session.getAttribute("userId"));
            result.put("username", session.getAttribute("username"));
            result.put("roleId", session.getAttribute("roleId"));
            result.put("roleName", session.getAttribute("roleName"));

            // Authorized tables via AuthenticationBean
            try {
                Object authBean = EJBLocator.lookupAuthenticationBean();
                Method m = authBean.getClass().getMethod("getUserAuthorizedTables", Integer.class);
                Object uid = session.getAttribute("userId");
                if (uid instanceof Integer) {
                    String[] tables = (String[]) m.invoke(authBean, (Integer) uid);
                    result.put("tables", Arrays.asList(tables));
                } else {
                    result.put("tables", Collections.emptyList());
                }
            } catch (Exception ex) {
                result.put("tables", Collections.emptyList());
            }

            // Extract directions (id, niveau, libelle)
            Object[] directions = (Object[]) session.getAttribute("directions");
            List<Map<String, Object>> dirList = new ArrayList<>();
            if (directions != null) {
                for (Object d : directions) {
                    try {
                        Method getId = d.getClass().getMethod("getIdDirection");
                        Method getNiveau = d.getClass().getMethod("getNiveau");
                        Method getLibelle = d.getClass().getMethod("getLibelle");
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", getId.invoke(d));
                        m.put("niveau", getNiveau.invoke(d));
                        m.put("libelle", getLibelle.invoke(d));
                        dirList.add(m);
                    } catch (Exception ignored) {}
                }
            }
            result.put("directions", dirList);

            // Extract actions (id, libelle)
            Object[] actions = (Object[]) session.getAttribute("actions");
            List<Map<String, Object>> actList = new ArrayList<>();
            if (actions != null) {
                for (Object a : actions) {
                    try {
                        Method getId = a.getClass().getMethod("getIdAction");
                        Method getLibelle = a.getClass().getMethod("getLibelle");
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", getId.invoke(a));
                        m.put("libelle", getLibelle.invoke(a));
                        actList.add(m);
                    } catch (Exception ignored) {}
                }
            }
            result.put("actions", actList);

            out.print(gson.toJson(result));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
