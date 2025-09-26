package com.banque.principale.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.banque.principale.model.Client;

public class ClientService {

    private Map<String, Client> clients = new HashMap<>();

    // BanqueService injecté
    private BanqueService banqueService;

    public ClientService() {
        // Constructeur par défaut (sans BanqueService)
        chargerClientsTest();
    }

    public ClientService(BanqueService banqueService) {
        this.banqueService = banqueService;
        chargerClientsTest();
    }

    private void chargerClientsTest() {
        ajouterClient(new Client("CLIENT001", "Dupont", "Jean"));
        ajouterClient(new Client("CLIENT002", "Martin", "Marie"));
        ajouterClient(new Client("CLIENT003", "Bernard", "Pierre"));

        System.out.println("📊 " + clients.size() + " clients de test chargés");
    }

    public void ajouterClient(Client client) {
        if (client != null && client.getNumeroClient() != null) {
            clients.put(client.getNumeroClient(), client);
        }
    }

    public Client getClient(String numeroClient) {
        return numeroClient == null ? null : clients.get(numeroClient);
    }

    public List<Client> getTousLesClients() {
        return new ArrayList<>(clients.values());
    }

    public int getNombreClients() {
        return clients.size();
    }

    public BanqueService getBanqueService() {
        return banqueService;
    }

    public void setBanqueService(BanqueService banqueService) {
        this.banqueService = banqueService;
    }
}
