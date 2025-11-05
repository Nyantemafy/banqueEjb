package com.interface_app.servlet;

import com.multiplication.session.SessionInfo;
import com.multiplication.dao.ValidationVirementDAORemote;
import com.multiplication.model.ValidationVirement;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/validations")
public class AdminValidationsServlet extends HttpServlet {

    @EJB(lookup = "ejb:/app2-multiplication/ValidationVirementDAOApp2!com.multiplication.dao.ValidationVirementDAORemote")
    private ValidationVirementDAORemote validationVirementDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("sessionInfo") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");
        if (!(sessionInfo.isAdmin() || sessionInfo.isAgentSup())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<ValidationVirement> validations = validationVirementDAO.listAll();
        req.setAttribute("validations", validations);
        req.getRequestDispatcher("/admin-validations.jsp").forward(req, resp);
    }
}
