package com.interface_app.servlet;

import com.multiplication.dao.CompteCourantDAORemote;
import com.multiplication.dao.TransactionDAORemote;
import com.multiplication.model.CompteCourant;
import com.multiplication.model.Transaction;
import com.multiplication.session.SessionInfo;
import com.devises.model.Change;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.devises.model.Devise;
import java.math.BigDecimal;
import java.util.List;
import javax.ws.rs.client.Entity;
import java.util.ArrayList;
import javax.ws.rs.core.GenericType;

@WebServlet("/client/dashboard")
public class ClientDashboardServlet extends HttpServlet {

    @EJB(lookup = "ejb:/app2-multiplication/CompteCourantDAOApp2!com.multiplication.dao.CompteCourantDAORemote")
    private CompteCourantDAORemote compteCourantDAO;

    @EJB(lookup = "ejb:/app2-multiplication/TransactionDAOApp2!com.multiplication.dao.TransactionDAORemote")
    private TransactionDAORemote transactionDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("sessionInfo") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");

        // Récupérer le compte du client (pas de requête supplémentaire, utiliser
        // sessionInfo)
        List<CompteCourant> comptes = compteCourantDAO.findByUtilisateur(sessionInfo.getIdUser());

        if (!comptes.isEmpty()) {
            CompteCourant compte = comptes.get(0);
            req.setAttribute("compte", compte);
            req.setAttribute("solde", compte.getSolde());

            // Récupérer l'historique des transactions par compte
            List<Transaction> transactions = transactionDAO.findByCompte(
                    compte.getIdCompteCourant());
            req.setAttribute("transactions", transactions);

            // Optionnel: conversion d'affichage de la liste des transactions
            String deviseAffichage = req.getParameter("deviseAffichage");
            String dateCours = req.getParameter("dateCours"); // optionnel: yyyy-MM-dd
            if (deviseAffichage != null && !deviseAffichage.trim().isEmpty()) {
                try {
                    BigDecimal tauxAffichage = getTauxChange(deviseAffichage, dateCours);
                    req.setAttribute("deviseAffichage", deviseAffichage);
                    req.setAttribute("tauxAffichage", tauxAffichage);
                    if (dateCours != null && !dateCours.trim().isEmpty()) {
                        req.setAttribute("dateCours", dateCours);
                    }
                } catch (Exception ex) {
                    // En cas d'erreur de récupération du taux, on ignore et on reste en AR
                    req.setAttribute("error", "Impossible de récupérer le taux pour " + deviseAffichage + ": " + ex.getMessage());
                }
            }
        }

        // Récupérer la liste des devises disponibles depuis app1 pour les listes déroulantes
        try {
            List<Devise> all = getAllDevises();
            List<String> noms = new ArrayList<>();
            for (Devise d : all) {
                noms.add(d.getNomDevise());
            }
            // Map last occurrence per code
            java.util.LinkedHashMap<String, Devise> lastByCode = new java.util.LinkedHashMap<>();
            for (Devise d : all) {
                lastByCode.put(d.getNomDevise(), d);
            }
            List<Devise> dedup = new ArrayList<>(lastByCode.values());
            req.setAttribute("listeDevises", noms);
            req.setAttribute("listeDevisesObj", all);
            req.setAttribute("listeDevisesDedup", dedup);
        } catch (Exception ex) {
            // En cas d'erreur, on ignore et on laisse les options par défaut côté JSP si présentes
        }

        req.getRequestDispatcher("/client-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("convertir".equals(action)) {
            String devise = req.getParameter("devise");
            String montantStr = req.getParameter("montant");

            try {
                BigDecimal montant = new BigDecimal(montantStr);
                BigDecimal taux = getTauxChange(devise);

                // Utiliser la méthode statique de Change pour effectuer la conversion
                Change change = Change.effectuerChange(montant, "AR", devise, taux);

                req.setAttribute("changeResult", change);
                req.setAttribute("message", "Conversion effectuée: " +
                        change.getMontantConverti() + " " + devise);
            } catch (Exception e) {
                req.setAttribute("error", "Erreur lors de la conversion: " + e.getMessage());
            }
        }
        else if ("ajouterDevise".equals(action)) {
            String nomDevise = req.getParameter("nomDevise");
            String dateDebut = req.getParameter("dateDebut");
            String dateFin = req.getParameter("dateFin");
            String coursStr = req.getParameter("cours");

            try {
                double cours = new BigDecimal(coursStr).doubleValue();
                Devise devise = new Devise(nomDevise, dateDebut, dateFin, cours);

                String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
                Client client = ClientBuilder.newClient();
                try {
                    WebTarget target = client.target(baseUrl).path("devises");
                    Response apiResp = target
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(devise, MediaType.APPLICATION_JSON_TYPE));
                    if (apiResp.getStatus() == 201) {
                        req.setAttribute("message", "Devise ajoutée avec succès.");
                    } else {
                        req.setAttribute("error", "Échec de l'ajout de la devise (" + apiResp.getStatus() + ")");
                    }
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                req.setAttribute("error", "Erreur lors de l'ajout de la devise: " + e.getMessage());
            }
        }

        doGet(req, resp);
    }

    /**
     * Récupère le taux de change depuis app1 via REST
     */
    private BigDecimal getTauxChange(String devise) {
        return getTauxChange(devise, null);
    }

    /**
     * Récupère le taux de change pour une devise et éventuellement une date (yyyy-MM-dd).
     * Si dateCours est null ou vide, on récupère le dernier cours.
     */
    private BigDecimal getTauxChange(String devise, String dateCours) {
        String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(baseUrl).path("devises").path(devise);
            if (dateCours != null && !dateCours.trim().isEmpty()) {
                target = target.queryParam("date", dateCours);
            }
            Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();
            if (resp.getStatus() == 200) {
                Devise d = resp.readEntity(Devise.class);
                return BigDecimal.valueOf(d.getCours());
            }
            return BigDecimal.ONE;
        } finally {
            client.close();
        }
    }

    private List<Devise> getAllDevises() {
        String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(baseUrl).path("devises");
            Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Devise>>() {});
            }
            return new ArrayList<>();
        } finally {
            client.close();
        }
    }
}