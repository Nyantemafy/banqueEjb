package com.interface_app.servlet;

import com.interface_app.ejb.InterfaceService;
import com.interface_app.model.Devise;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/deviseDetail")
public class InterfaceServlet extends HttpServlet {

    @EJB
    private InterfaceService interfaceService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String deviseNom = req.getParameter("devise");

        if (deviseNom != null && !deviseNom.isEmpty()) {
            Devise devise = interfaceService.getDeviseDetail(deviseNom);
            req.setAttribute("devise", devise);
        }

        req.setAttribute("noms", interfaceService.getNomsDevises());
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}