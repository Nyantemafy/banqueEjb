package com.devises.ejb;

import com.devises.model.Devise;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Stateless
public class DeviseServiceBean implements DeviseService {

    private static final String FILE_NAME = "devises.txt";

    private String resolveFilePath() {
        String override = System.getProperty("devises.file");
        if (override != null && !override.trim().isEmpty()) {
            return override.trim();
        }
        String base = System.getProperty("jboss.server.data.dir", "/opt/jboss/wildfly/standalone/data");
        return base + "/" + FILE_NAME;
    }

    @Override
    public List<Devise> getAllDevises() {
        List<Devise> devises = new ArrayList<>();
        String filePath = resolveFilePath();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return devises;
        }

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
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
        Devise last = null;
        for (Devise d : devises) {
            if (d.getNomDevise().equalsIgnoreCase(nom)) {
                last = d; // keep updating to end up with the last occurrence
            }
        }
        return last;
    }

    @Override
    public void addDevise(Devise devise) {
        try {
            String filePath = resolveFilePath();
            Path path = Paths.get(filePath);
            // Ensure parent directory exists
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            // Create file if it does not exist
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            String line = String.format("%s;%s;%s;%s",
                    devise.getNomDevise(),
                    devise.getDateDebut(),
                    devise.getDateFin(),
                    Double.toString(devise.getCours()));

            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND)) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}