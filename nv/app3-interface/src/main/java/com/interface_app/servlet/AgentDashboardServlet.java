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

        req.getRequestDispatcher("/WEB-INF/jsp/agent-dashboard.jsp").forward(req, resp);
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

                Transaction transaction = virementService.effectuerVirement(
                        Integer.parseInt(compteEmetteur),
                        compteBeneficiaire,
                        montant,
                        devise,
                        date);

                req.setAttribute("message", "Virement effectué avec succès. Référence: " +
                        transaction.getIdTransaction());
            } else if ("changerDevise".equals(action) || "correctionAvant".equals(action)) {
                String idTransaction = req.getParameter("idTransaction");
                String nouvelleDevise = req.getParameter("nouvelleDevise");
                String taux = req.getParameter("taux");

                changeService.correctionAvant(
                        Integer.parseInt(idTransaction),
                        nouvelleDevise,
                        new BigDecimal(taux));

                req.setAttribute("message", "Correction avant appliquée: devise modifiée");
            } else if ("correctionApres".equals(action)) {
                String idTransaction = req.getParameter("idTransaction");
                String nouvelleDevise = req.getParameter("nouvelleDevise");
                String taux = req.getParameter("taux");

                changeService.correctionApres(
                        Integer.parseInt(idTransaction),
                        nouvelleDevise,
                        new BigDecimal(taux));

                req.setAttribute("message", "Correction après appliquée: annulation et nouvelle opération");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Erreur: " + e.getMessage());
        }

        doGet(req, resp);
    }
}