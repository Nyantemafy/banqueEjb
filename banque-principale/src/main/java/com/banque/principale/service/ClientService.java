package com.banque.principale.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.banque.principale.model.Client;

public class ClientService {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/banque_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "antema";

    private BanqueService banqueService;

    public ClientService() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientService(BanqueService banqueService) {
        this();
        this.banqueService = banqueService;
    }

    public void ajouterClient(Client client) {
        if (client == null || client.getNumeroClient() == null) return;
        String sql = "INSERT INTO clients (numero_client, nom, prenom, email, telephone, date_inscription, role, mot_de_passe) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (numero_client) DO UPDATE SET nom = EXCLUDED.nom, prenom = EXCLUDED.prenom, email = EXCLUDED.email, telephone = EXCLUDED.telephone, role = EXCLUDED.role, mot_de_passe = EXCLUDED.mot_de_passe";
        try (Connection cnx = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, client.getNumeroClient());
            ps.setString(2, client.getNom());
            ps.setString(3, client.getPrenom());
            ps.setString(4, client.getEmail());
            ps.setString(5, client.getTelephone());
            ps.setTimestamp(6, new Timestamp(client.getDateInscription() != null ? client.getDateInscription().getTime() : System.currentTimeMillis()));
            ps.setString(7, client.getRole());
            ps.setString(8, client.getMotDePasse());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Client getClient(String numeroClient) {
        if (numeroClient == null) return null;
        String sql = "SELECT numero_client, nom, prenom, email, telephone, date_inscription, role, mot_de_passe FROM clients WHERE numero_client = ?";
        try (Connection cnx = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, numeroClient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = new Client();
                    c.setNumeroClient(rs.getString(1));
                    c.setNom(rs.getString(2));
                    c.setPrenom(rs.getString(3));
                    c.setEmail(rs.getString(4));
                    c.setTelephone(rs.getString(5));
                    Timestamp ts = rs.getTimestamp(6);
                    if (ts != null) c.setDateInscription(new java.util.Date(ts.getTime()));
                    c.setRole(rs.getString(7));
                    c.setMotDePasse(rs.getString(8));
                    return c;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Client> getTousLesClients() {
        String sql = "SELECT numero_client, nom, prenom, email, telephone, date_inscription, role, mot_de_passe FROM clients ORDER BY numero_client";
        List<Client> list = new ArrayList<>();
        try (Connection cnx = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Client c = new Client();
                c.setNumeroClient(rs.getString(1));
                c.setNom(rs.getString(2));
                c.setPrenom(rs.getString(3));
                c.setEmail(rs.getString(4));
                c.setTelephone(rs.getString(5));
                Timestamp ts = rs.getTimestamp(6);
                if (ts != null) c.setDateInscription(new java.util.Date(ts.getTime()));
                c.setRole(rs.getString(7));
                c.setMotDePasse(rs.getString(8));
                list.add(c);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getNombreClients() {
        String sql = "SELECT COUNT(*) FROM clients";
        try (Connection cnx = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BanqueService getBanqueService() {
        return banqueService;
    }

    public void setBanqueService(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    public boolean isAdmin(String numeroClient) {
        if (numeroClient == null || numeroClient.isEmpty()) return false;
        String sql = "SELECT role FROM clients WHERE numero_client = ?";
        try (Connection cnx = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, numeroClient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString(1);
                    return role != null && role.equalsIgnoreCase("ADMIN");
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
