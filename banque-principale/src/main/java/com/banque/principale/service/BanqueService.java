package com.banque.principale.service;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.Properties;

// Interfaces des EJB distants
import com.banque.comptecourant.ejb.CompteCourantRemote;
import com.banque.pret.ejb.PretRemote;

public class BanqueService {

    private CompteCourantRemote compteCourantService;
    private PretRemote pretService;
    // Note: Pour .NET, nous utiliserons des appels REST/SOAP

    public BanqueService() {
        initializeEJBServices();
    }

    private void initializeEJBServices() {
        try {
            Properties props = new Properties();
            props.put("java.naming.factory.initial",
                    "org.jboss.naming.remote.client.InitialContextFactory");
            props.put("java.naming.provider.url", "remote+http://localhost:8080");
            props.put("jboss.naming.client.ejb.context", true);

            InitialContext ctx = new InitialContext(props);

            // Lookup des EJB
            compteCourantService = (CompteCourantRemote) ctx.lookup(
                    "ejb:/compte-courant//CompteCourantBean!com.banque.comptecourant.ejb.CompteCourantRemote");

            pretService = (PretRemote) ctx.lookup(
                    "ejb:/prets//PretBean!com.banque.pret.ejb.PretRemote");

            System.out.println("Services EJB initialisés avec succès");

        } catch (NamingException e) {
            System.err.println("Erreur initialisation EJB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // === SERVICES COMPTE COURANT ===

    public BigDecimal consulterSoldeCompteCourant(String numeroCompte) {
        try {
            return compteCourantService.consulterSolde(numeroCompte);
        } catch (Exception e) {
            System.err.println("Erreur consultation solde CC: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public boolean deposerCompteCourant(String numeroCompte, BigDecimal montant) {
        try {
            return compteCourantService.deposer(numeroCompte, montant, "Dépôt via interface principale");
        } catch (Exception e) {
            System.err.println("Erreur dépôt CC: " + e.getMessage());
            return false;
        }
    }

    public boolean retirerCompteCourant(String numeroCompte, BigDecimal montant) {
        try {
            return compteCourantService.retirer(numeroCompte, montant, "Retrait via interface principale");
        } catch (Exception e) {
            System.err.println("Erreur retrait CC: " + e.getMessage());
            return false;
        }
    }

    // === SERVICES COMPTE DÉPÔT (.NET via REST) ===

    public BigDecimal consulterSoldeCompteDepot(String numeroCompte) {
        // Appel REST vers le service .NET
        // Implementation avec HttpClient ou Jersey
        try {
            // URL du service WCF/REST
            String url = "http://localhost:8081/CompteDepot/ConsulterSolde?numeroCompte=" + numeroCompte;
            // ... code d'appel REST ...
            return BigDecimal.ZERO; // Placeholder
        } catch (Exception e) {
            System.err.println("Erreur consultation dépôt: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // === SERVICES PRÊTS ===

    public String demanderPret(String numeroClient, BigDecimal montant, int duree, String objet) {
        try {
            return

    pretService.soumettreDemandeP ret(numeroClient, montant, duree, objet);
        }catch(

    Exception e)
    {
        System.err.println("Erreur demande prêt: " + e.getMessage());
        return null;
    }
    }

    public BigDecimal calculerMensualitePret(BigDecimal montant, int duree) {
        try {
            return pretService.calculerMensualite(montant, 4.5, duree);
        } catch (Exception e) {
            System.err.println("Erreur calcul mensualité: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

// === OPERATIONS MULTI-SERVICES ===

public String getSyntheseClient(String numeroClient) {
        StringBuilder synthese = new StringBuilder();
        
        // Solde compte courant
        BigDecimal soldeCC = consulterSoldeCompteCourant(numeroClient + "-CC");
        synthese.append("Compte Courant: ").append(soldeCC).appen