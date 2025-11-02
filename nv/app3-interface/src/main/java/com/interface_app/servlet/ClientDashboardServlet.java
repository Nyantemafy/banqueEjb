package com.interface_app.servlet;

import com.multiplication.dao.CompteCourantDAO;
import com.multiplication.dao.TransactionDAO;
import com.multiplication.model.CompteCourant;
import com.multiplication.model.Transaction;
import com.multiplication.session.SessionInfo;
import com.multiplication.metier.Change;
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
import com.interface_app.model.Devise;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/client/dashboard")
public class ClientDashboardServlet extends HttpServlet {

    @EJB
    private CompteCourantDAO compteCourantDAO;

    @EJB
    private TransactionDAO transactionDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("sessionInfo") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");

        // Récupérer le compte du client
        List<CompteCourant> comptes = compteCourantDAO.findByUtilisateur(sessionInfo.getIdUser());

        if (!comptes.isEmpty()) {
            CompteCourant compte = comptes.get(0);
            req.setAttribute("compte", compte);
            req.setAttribute("solde", compte.getSolde());

            // Récupérer l'historique des transactions par compte
            List<Transaction> transactions = transactionDAO.findByCompte(
                    compte.getIdCompteCourant());
            req.setAttribute("transactions", transactions);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/client-dashboard.jsp").forward(req, resp);
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
                BigDecimal taux = getTauxChange(devise); // Méthode à implémenter

                Change change = new Change().effectuerChange(
                        montant, "AR", devise, taux);

                req.setAttribute("changeResult", change);
                req.setAttribute("message", "Conversion effectuée: " +
                        change.getMontantConverti() + " " + devise);
            } catch (Exception e) {
                req.setAttribute("error", "Erreur lors de la conversion: " + e.getMessage());
            }
        }

        doGet(req, resp);
    }

    private BigDecimal getTauxChange(String devise) {
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
}