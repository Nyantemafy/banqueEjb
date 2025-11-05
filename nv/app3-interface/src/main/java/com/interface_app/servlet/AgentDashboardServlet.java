package com.interface_app.servlet;

import com.multiplication.ejb.VirementService;
import com.multiplication.ejb.ChangeService;
import com.multiplication.session.SessionInfo;
import com.multiplication.model.Transaction;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.devises.model.Devise;
import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.core.GenericType;

@WebServlet("/agent/dashboard")
public class AgentDashboardServlet extends HttpServlet {

    @EJB(lookup = "ejb:/app2-multiplication/VirementServiceBean!com.multiplication.ejb.VirementService")
    private VirementService virementService;

    @EJB(lookup = "ejb:/app2-multiplication/ChangeServiceBeanApp2!com.multiplication.ejb.ChangeService")
    private ChangeService changeService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("sessionInfo") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");

        if (!sessionInfo.isAgent()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Charger la liste des devises depuis app1 pour alimenter les listes déroulantes
        try {
            List<Devise> all = getAllDevises();
            List<String> noms = new ArrayList<>();
            for (Devise d : all) noms.add(d.getNomDevise());
            // Dédupliquer en gardant la dernière occurrence par code
            java.util.LinkedHashMap<String, Devise> lastBy = new java.util.LinkedHashMap<>();
            for (Devise d : all) {
                lastBy.put(d.getNomDevise(), d);
            }
            List<Devise> dedup = new ArrayList<>(lastBy.values());
            req.setAttribute("listeDevises", noms);
            req.setAttribute("listeDevisesObj", all);
            req.setAttribute("listeDevisesDedup", dedup);
        } catch (Exception ignored) {}

        req.getRequestDispatcher("/agent-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        try {
            if ("effectuerVirement".equals(action)) {
                String compteEmetteur = req.getParameter("compteEmetteur");
                String compteBeneficiaire = req.getParameter("compteBeneficiaire");
                String montant = req.getParameter("montant");
                String devise = req.getParameter("devise");
                String date = req.getParameter("date");

                // Effectuer le virement (tous les contrôles sont dans le service)
                SessionInfo sessionInfo = (SessionInfo) req.getSession(false).getAttribute("sessionInfo");
                Transaction transaction = virementService.effectuerVirement(
                        sessionInfo.getIdUser(),
                        Integer.parseInt(compteEmetteur),
                        compteBeneficiaire,
                        montant,
                        devise,
                        date);

                req.setAttribute("message", "Virement effectué avec succès. Référence: " +
                        transaction.getIdTransaction());

            } else if ("changerDevise".equals(action)) {
                String idTransaction = req.getParameter("idTransaction");
                String nouvelleDevise = req.getParameter("nouvelleDevise");
                // Déterminer un taux automatique (dernier cours) en supposant la devise source AR
                BigDecimal tauxAuto = getTauxDernier("AR", nouvelleDevise);
                // Changer la devise d'une transaction en attente (correction avant)
                changeService.correctionAvant(
                        Integer.parseInt(idTransaction),
                        nouvelleDevise,
                        tauxAuto);

                req.setAttribute("message", "Devise modifiée avec succès");
            } else if ("ajouterCours".equals(action)) {
                String deviseSource = req.getParameter("deviseSource");
                String deviseCible = req.getParameter("deviseCible");
                String montant = req.getParameter("montantCours");

                // Calcul du taux entre source et cible en se basant sur le dernier cours (référencé AR)
                BigDecimal taux = getTauxDernier(deviseSource, deviseCible);
                changeService.effectuerChange(new BigDecimal(montant), deviseSource, deviseCible, taux);
                req.setAttribute("message", "Nouveau cours enregistré dans le fichier de changes");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Erreur: " + e.getMessage());
        }

        doGet(req, resp);
    }

    private BigDecimal getTauxDernier(String deviseSource, String deviseCible) {
        // Si même devise
        if (deviseSource.equalsIgnoreCase(deviseCible)) return BigDecimal.ONE;

        // Récupère cours par rapport à AR, puis calcule source->cible = cours(cible)/cours(source)
        BigDecimal coursSource = getCoursDernier(deviseSource);
        BigDecimal coursCible = getCoursDernier(deviseCible);
        if (coursSource.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ONE;
        return coursCible.divide(coursSource, 8, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal getCoursDernier(String devise) {
        if ("AR".equalsIgnoreCase(devise)) return BigDecimal.ONE; // base
        String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(baseUrl).path("devises").path(devise);
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

    private java.util.List<Devise> getAllDevises() {
        String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(baseUrl).path("devises");
            Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<java.util.List<Devise>>() {});
            }
            return new ArrayList<>();
        } finally {
            client.close();
        }
    }
}