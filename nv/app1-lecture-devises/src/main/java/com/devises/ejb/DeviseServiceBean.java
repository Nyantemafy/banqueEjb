package com.devises.ejb;

import com.devises.model.Devise;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class DeviseServiceBean implements DeviseService {

    private static final String FILE_PATH = "/opt/jboss/wildfly/standalone/deployments/devises.txt";

    @Override
    public List<Devise> getAllDevises() {
        List<Devise> devises = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    Devise devise = new Devise(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            Double.parseDouble(parts[3].trim()));
                    devises.add(devise);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return devises;
    }

    @Override
    public Devise getDeviseByNom(String nom) {
        List<Devise> devises = getAllDevises();
        for (Devise d : devises) {
            if (d.getNomDevise().equalsIgnoreCase(nom)) {
                return d;
            }
        }
        return null;
    }
}