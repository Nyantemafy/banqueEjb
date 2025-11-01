package com.interface_app.ejb;

import com.interface_app.model.Devise;
import com.multiplication.ejb.MultiplicationService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class InterfaceServiceBean implements InterfaceService {

    @EJB(lookup = "java:global/app2-multiplication/MultiplicationServiceBean!com.multiplication.ejb.MultiplicationService")
    private MultiplicationService multiplicationService;

    @Override
    public List<String> getNomsDevises() {
        List<String> noms = new ArrayList<>();
        List<com.multiplication.model.Devise> devises = multiplicationService.getDevisesMultipliees();

        for (com.multiplication.model.Devise d : devises) {
            noms.add(d.getNomDevise());
        }

        return noms;
    }

    @Override
    public Devise getDeviseDetail(String nom) {
        com.multiplication.model.Devise d = multiplicationService.getDeviseMultipliee(nom);

        if (d != null) {
            return new Devise(
                    d.getNomDevise(),
                    d.getDateDebut(),
                    d.getDateFin(),
                    d.getCours());
        }

        return null;
    }
}