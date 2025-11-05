package com.interface_app.servlet;

import com.multiplication.dao.TransactionDAORemote;
import com.multiplication.ejb.VirementService;
import com.multiplication.session.SessionInfo;
import com.multiplication.model.Transaction;
import com.multiplication.dao.HistoriqueDAORemote;
import com.multiplication.model.Historique;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @EJB(lookup = "ejb:/app2-multiplication/TransactionDAOApp2!com.multiplication.dao.TransactionDAORemote")
    private TransactionDAORemote transactionDAO;

    @EJB(lookup = "ejb:/app2-multiplication/VirementServiceBean!com.multiplication.ejb.VirementService")
    private VirementService virementService;

    @EJB(lookup = "ejb:/app2-multiplication/HistoriqueDAOApp2!com.multiplication.dao.HistoriqueDAORemote")
    private HistoriqueDAORemote historiqueDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("sessionInfo") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");

        // Vérifier le rôle (pas besoin de requête DB, tout est dans sessionInfo)
        if (!sessionInfo.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Récupérer tous les virements en attente
        List<Transaction> transactionsEnAttente = transactionDAO.findVirementsEnAttente();
        req.setAttribute("transactionsEnAttente", transactionsEnAttente);

        // Récupérer tous les virements (tous statuts)
        List<Transaction> toutesTransactions = transactionDAO.findAllVirements();
        req.setAttribute("toutesTransactions", toutesTransactions);

        // Récupérer historiques récents
        List<Historique> historiques = historiqueDAO.findRecent(50);
        req.setAttribute("historiques", historiques);

        req.getRequestDispatcher("/admin-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String idTransactionStr = req.getParameter("idTransaction");

        try {
            Integer idTransaction = Integer.parseInt(idTransactionStr);

            if ("valider".equals(action)) {
                // Valider une transaction en attente
                SessionInfo sessionInfo = (SessionInfo) req.getSession(false).getAttribute("sessionInfo");
                virementService.validerVirement(sessionInfo.getIdUser(), idTransaction);
                req.setAttribute("message", "Transaction validée avec succès");

            } else if ("annulerAvant".equals(action)) {
                // Annuler une transaction en attente
                virementService.annulerVirementAvant(idTransaction);
                req.setAttribute("message", "Transaction annulée");

            } else if ("annulerApres".equals(action)) {
                // Annuler une transaction validée (virement inverse)
                virementService.annulerVirementApres(idTransaction);
                req.setAttribute("message", "Transaction annulée avec virement inverse effectué");
            }
        } catch (Exception e) {
            req.setAttribute("error", "Erreur: " + e.getMessage());
        }

        doGet(req, resp);
    }
}