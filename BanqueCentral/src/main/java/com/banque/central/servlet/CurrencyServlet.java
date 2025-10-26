package com.banque.central.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.banque.central.util.EJBLocator;
import com.google.gson.Gson;

@WebServlet("/api/currencies")
public class CurrencyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CurrencyServlet.class.getName());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Object> result = new HashMap<>();

        try {
            LOGGER.info("Fetching currencies via CompteCourant...");

            // Appeler CompteCourant qui gère Change en remote
            Object compteBean = EJBLocator.lookupCompteBean();

            // getDefaultCurrency()
            Method getDefaultMethod = compteBean.getClass().getMethod("getDefaultCurrency");
            String defaultCurrency = (String) getDefaultMethod.invoke(compteBean);

            // getAvailableCurrencies()
            Method getCurrenciesMethod = compteBean.getClass().getMethod("getAvailableCurrencies");
            @SuppressWarnings("unchecked")
            List<String> currencies = (List<String>) getCurrenciesMethod.invoke(compteBean);

            result.put("default", defaultCurrency);
            result.put("currencies", currencies);
            result.put("success", true);

            LOGGER.info("Currencies retrieved successfully: " + currencies);
            out.print(gson.toJson(result));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching currencies", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("error", "Service de change non disponible");
            result.put("default", "MGA");
            result.put("currencies", Arrays.asList("MGA", "EUR", "USD", "KMF", "ZAR"));
            out.print(gson.toJson(result));
        }
    }
}