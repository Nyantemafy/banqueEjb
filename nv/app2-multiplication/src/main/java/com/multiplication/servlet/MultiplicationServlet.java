package com.multiplication.servlet;

import com.multiplication.ejb.MultiplicationService;
import com.multiplication.model.Devise;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/multiplication")
public class MultiplicationServlet extends HttpServlet {

    @EJB
    private MultiplicationService multiplicationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("");
        out.println("Devises Multipliées par 2");
        out.println("");
        out.println("NomDate DébutDate FinCours x2");

        List<Devise> devises = multiplicationService.getDevisesMultipliees();
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