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
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            LOGGER.info("Attempting to lookup ChangeBean...");
            Object changeBean = EJBLocator.lookupChangeBean();
            LOGGER.info("ChangeBean lookup successful: " + changeBean.getClass().getName());

            // Test 1: getDefaultCurrency
            LOGGER.info("Calling getDefaultCurrency()...");
            Method getDefaultMethod = changeBean.getClass().getMethod("getDefaultCurrency");
            Object defaultCurrencyObj = getDefaultMethod.invoke(changeBean);
            String defaultCurrency = (defaultCurrencyObj != null) ? defaultCurrencyObj.toString() : "MGA";
            LOGGER.info("Default currency: " + defaultCurrency);

            // Test 2: getCurrencies
            LOGGER.info("Calling getCurrencies()...");
            Method getCurrenciesMethod = changeBean.getClass().getMethod("getCurrencies");
            Object currenciesObj = getCurrenciesMethod.invoke(changeBean);

            List<String> currencies = new ArrayList<>();
            if (currenciesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<?> rawList = (List<?>) currenciesObj;
                for (Object item : rawList) {
                    if (item != null) {
                        currencies.add(item.toString());
                    }
                }
                LOGGER.info("Currencies retrieved: " + currencies);
            } else if (currenciesObj != null) {
                LOGGER.warning("getCurrencies returned unexpected type: " + currenciesObj.getClass().getName());
                // Fallback: parse toString
                String str = currenciesObj.toString();
                if (str.startsWith("[") && str.endsWith("]")) {
                    str = str.substring(1, str.length() - 1);
                    for (String item : str.split(",")) {
                        currencies.add(item.trim());
                    }
                }
            }

            // Si aucune devise n'a été récupérée, utiliser des valeurs par défaut
            if (currencies.isEmpty()) {
                LOGGER.warning("No currencies retrieved, using defaults");
                currencies = Arrays.asList("MGA", "EUR", "USD", "KMF", "ZAR");
            }

            result.put("default", defaultCurrency);
            result.put("currencies", currencies);
            result.put("success", true);

            LOGGER.info("Sending response: " + result);
            out.print(gson.toJson(result));

        } catch (javax.naming.NamingException e) {
            LOGGER.log(Level.SEVERE, "JNDI lookup failed", e);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            result.put("success", false);
            result.put("error", "Service de change non disponible: " + e.getMessage());
            result.put("default", "MGA");
            result.put("currencies", Arrays.asList("MGA", "EUR", "USD", "KMF", "ZAR"));
            out.print(gson.toJson(result));

        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.log(Level.SEVERE, "Method invocation failed", cause != null ? cause : e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("error", "Erreur d'invocation: " + (cause != null ? cause.getMessage() : e.getMessage()));
            result.put("default", "MGA");
            result.put("currencies", Arrays.asList("MGA", "EUR", "USD", "KMF", "ZAR"));
            out.print(gson.toJson(result));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in CurrencyServlet", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("error", "Erreur inattendue: " + e.getClass().getName() + " - " + e.getMessage());
            result.put("default", "MGA");
            result.put("currencies", Arrays.asList("MGA", "EUR", "USD", "KMF", "ZAR"));

            // Ajouter la stack trace en mode debug
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            result.put("stackTrace", sw.toString());

            out.print(gson.toJson(result));
        }
    }

    // Méthode helper pour débugger
    private void logObjectDetails(Object obj) {
        if (obj == null) {
            LOGGER.info("Object is null");
            return;
        }

        LOGGER.info("Object class: " + obj.getClass().getName());
        LOGGER.info("Object toString: " + obj.toString());
        LOGGER.info("ClassLoader: " + obj.getClass().getClassLoader());
    }
}