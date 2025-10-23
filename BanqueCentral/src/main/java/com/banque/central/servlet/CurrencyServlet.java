package com.banque.central.servlet;

import com.banque.central.util.EJBLocator;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/currencies")
public class CurrencyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Object changeBean = EJBLocator.lookupChangeBean();
            Method getDefault = changeBean.getClass().getMethod("getDefaultCurrency");
            Method getAll = changeBean.getClass().getMethod("getCurrencies");
            String def = (String) getDefault.invoke(changeBean);
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) getAll.invoke(changeBean);
            Map<String, Object> res = new HashMap<>();
            res.put("default", def);
            res.put("currencies", list);
            out.print(gson.toJson(res));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
