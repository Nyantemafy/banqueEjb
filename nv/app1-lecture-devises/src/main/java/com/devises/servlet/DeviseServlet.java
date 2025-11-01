package com.devises.servlet;

import com.devises.ejb.DeviseService;
import com.devises.model.Devise;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/devises")
public class DeviseServlet extends HttpServlet {

    @EJB
    private DeviseService deviseService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("");
        out.println("Liste des Devises");
        out.println("");
        out.println("NomDate DébutDate FinCours");

        List<Devise> devises = deviseService.getAllDevises();
        for (Devise d : devises) {
            out.println("");
            out.println("" + d.getNomDevise() + "");
            out.println("" + d.getDateDebut() + "");
            out.println("" + d.getDateFin() + "");
            out.println("" + d.getCours() + "");
            out.println("");
        }

        out.println("");
        out.println("");
    }
}