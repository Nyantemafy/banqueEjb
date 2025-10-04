package com.banque.principale.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.banque.comptecourant.ejb.CompteCourantRemote;
import com.banque.comptecourant.model.Transaction;
import com.banque.pret.ejb.PretRemote;
import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;
import com.banque.principale.model.Client;

public class BanqueService {

    // Services EJB
    private CompteCourantRemote compteCourantService;
    private PretRemote pretService;

    // Service .NET (Minimal API)
    private static final String DEPOT_SERVICE_URL = "http://localhost:5000";

    // Service client (injection)
    private ClientService clientService;

    public BanqueService(ClientService clientService) {
        this.clientService = clientService;
        initializeEJBServices();
    }

    private void initializeEJBServices() {
        try {
            System.out.println("🔗 Connexion aux services EJB...");

            Properties props = new Properties();
            props.put("java.naming.factory.initial", "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put("java.naming.provider.url", "remote+http://localhost:8080");

            InitialContext ctx = new InitialContext(props);

            compteCourantService = (CompteCourantRemote) ctx.lookup(
                    "ejb:/compte-courant-1.0//CompteCourantBean!com.banque.comptecourant.ejb.CompteCourantRemote");

            pretService = (PretRemote) ctx.lookup(
                    "ejb:/application-prets-1.0//PretBean!com.banque.pret.ejb.PretRemote");

            System.out.println("✅ Services EJB connectés avec succès");

        } catch (NamingException e) {
            System.err.println("❌ Erreur connexion EJB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =================== CLIENTS ===================

    public boolean creerClient(Client client) {
        try {
            clientService.ajouterClient(client);

            boolean ccOk = compteCourantService.creerCompte(client.getNumeroCompteCourant(), client.getNomComplet());
            boolean depotOk = creerCompteDepot(client.getNumeroCompteDepot(), client.getNomComplet(), 2.5);

            if (ccOk && depotOk) {
                System.out.println("✅ Client créé avec tous ses comptes");
            } else {
                System.out.println("⚠️ Problème création comptes (CC:" + ccOk + ", Depot:" + depotOk + ")");
            }
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erreur création client: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Client getClient(String numeroClient) {
        Client client = clientService.getClient(numeroClient);
        if (client != null) {
            client.setSoldeCompteCourant(consulterSoldeCompteCourant(client.getNumeroCompteCourant()));
            client.setSoldeCompteDepot(consulterSoldeCompteDepot(client.getNumeroCompteDepot()));
        }
        return client;
    }

    // =================== COMPTE COURANT ===================

    public BigDecimal consulterSoldeCompteCourant(String numeroCompte) {
        try {
            return compteCourantService.consulterSolde(numeroCompte);
        } catch (Exception e) {
            System.err.println("❌ Erreur consultation CC: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public boolean deposerCompteCourant(String numeroCompte, BigDecimal montant) {
        try {
            return compteCourantService.deposer(numeroCompte, montant);
        } catch (Exception e) {
            System.err.println("❌ Erreur dépôt CC: " + e.getMessage());
            return false;
        }
    }

    public boolean retirerCompteCourant(String numeroCompte, BigDecimal montant) {
        try {
            return compteCourantService.retirer(numeroCompte, montant);
        } catch (Exception e) {
            System.err.println("❌ Erreur retrait CC: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getHistoriqueCompteCourant(String numeroCompte) {
        try {
            return compteCourantService.getHistorique(numeroCompte);
        } catch (Exception e) {
            System.err.println("❌ Erreur historique CC: " + e.getMessage());
            return null;
        }
    }

    // =================== COMPTE DEPOT (.NET) ===================

    public BigDecimal consulterSoldeCompteDepot(String numeroCompte) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String numeroEnc = URLEncoder.encode(numeroCompte, StandardCharsets.UTF_8.name());
            HttpGet request = new HttpGet(DEPOT_SERVICE_URL + "/solde/" + numeroEnc);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());
                return new BigDecimal(result.replace("\"", ""));
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur consultation dépôt .NET: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public boolean creerCompteDepot(String numeroCompte, String proprietaire, double tauxInteret) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String numeroEnc = URLEncoder.encode(numeroCompte, StandardCharsets.UTF_8.name());
            String proprietaireEnc = URLEncoder.encode(proprietaire, StandardCharsets.UTF_8.name());
            String tauxStr = String.format("%.2f", tauxInteret);
            HttpPost request = new HttpPost(DEPOT_SERVICE_URL + 
                    "/creercompte?numero=" + numeroEnc + "&proprietaire=" + proprietaireEnc + "&taux=" + tauxStr);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());
                return Boolean.parseBoolean(result.replace("\"", ""));
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur création dépôt .NET: " + e.getMessage());
            return false;
        }
    }

    public boolean deposerCompteDepot(String numeroCompte, BigDecimal montant) {
        return operationDepotRetrait(numeroCompte, montant, "Deposer");
    }

    public boolean retirerCompteDepot(String numeroCompte, BigDecimal montant) {
        return operationDepotRetrait(numeroCompte, montant, "Retirer");
    }

    private boolean operationDepotRetrait(String numeroCompte, BigDecimal montant, String action) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String path = action.equalsIgnoreCase("Deposer") ? "/depot" : "/retrait";
            String numeroEnc = URLEncoder.encode(numeroCompte, StandardCharsets.UTF_8.name());
            String montantEnc = URLEncoder.encode(montant.toPlainString(), StandardCharsets.UTF_8.name());
            HttpPost request = new HttpPost(DEPOT_SERVICE_URL + path + "?numero=" + numeroEnc + "&montant=" + montantEnc);
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                String result = EntityUtils.toString(response.getEntity());
                return Boolean.parseBoolean(result.replace("\"", ""));
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur " + action + " dépôt .NET: " + e.getMessage());
            return false;
        }
    }

    // =================== PRÊTS ===================

    public String demanderPret(String numeroClient, BigDecimal montant, int dureeEnMois, String objet) {
        try {
            return pretService.demanderPret(numeroClient, montant, dureeEnMois, objet);
        } catch (Exception e) {
            System.err.println("❌ Erreur demande prêt: " + e.getMessage());
            return null;
        }
    }

    public BigDecimal calculerMensualitePret(BigDecimal montant, int dureeEnMois) {
        try {
            return pretService.calculerMensualite(montant, 4.5, dureeEnMois);
        } catch (Exception e) {
            System.err.println("❌ Erreur calcul mensualité: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public boolean approuverDemandePret(String numeroDemande) {
        try {
            return pretService.approuverDemande(numeroDemande);
        } catch (Exception e) {
            System.err.println("❌ Erreur approbation: " + e.getMessage());
            return false;
        }
    }

    public boolean rejeterDemandePret(String numeroDemande, String motif) {
        try {
            return pretService.rejeterDemande(numeroDemande, motif);
        } catch (Exception e) {
            System.err.println("❌ Erreur rejet: " + e.getMessage());
            return false;
        }
    }

    public List<DemandePret> getDemandesEnAttente() {
        try {
            return pretService.getDemandesEnAttente();
        } catch (Exception e) {
            System.err.println("❌ Erreur récupération demandes: " + e.getMessage());
            return null;
        }
    }

    public List<Pret> getPretsClient(String numeroClient) {
        try {
            return pretService.getPretsClient(numeroClient);
        } catch (Exception e) {
            System.err.println("❌ Erreur récupération prêts: " + e.getMessage());
            return null;
        }
    }

}
