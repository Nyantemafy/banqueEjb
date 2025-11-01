package com.multiplication.ejb;

import com.devises.ejb.DeviseService;
import com.multiplication.model.Devise;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Stateless
public class MultiplicationServiceBean implements MultiplicationService {

    private boolean useRestMode() {
        String mode = System.getProperty("client.mode", "ejb");
        return "rest".equalsIgnoreCase(mode);
    }

    private DeviseService getRemoteDeviseService() {
        try {
            Hashtable<String, String> jndiProperties = new Hashtable<>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
                    "org.wildfly.naming.client.WildFlyInitialContextFactory");

            jndiProperties.put(Context.PROVIDER_URL,
                    "remote+http://127.0.0.1:8081");

            jndiProperties.put("jboss.naming.client.ejb.context", "true");

            // Ajouter les credentials
            jndiProperties.put(Context.SECURITY_PRINCIPAL, "ejbuser");
            jndiProperties.put(Context.SECURITY_CREDENTIALS, "ejbpass");

            // Options SASL pour forcer l'auth par user/password
            jndiProperties.put("wildfly.naming.client.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
            jndiProperties.put("wildfly.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
            jndiProperties.put("wildfly.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");

            Context context = new InitialContext(jndiProperties);

            return (DeviseService) context.lookup(
                    "ejb:/app1-devises/DeviseServiceBean!com.devises.ejb.DeviseService");

        } catch (Exception e) {
            System.err.println("Erreur de connexion EJB Remote:");
            e.printStackTrace();
            return null;
        }
    }

    private List<com.devises.model.Devise> fetchAllViaRest() {
        String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(baseUrl).path("devises");
            Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<com.devises.model.Devise>>() {});
            }
            System.err.println("REST getAll status: " + resp.getStatus());
            return new ArrayList<>();
        } finally {
            client.close();
        }
    }

    private com.devises.model.Devise fetchByNomViaRest(String nom) {
        String baseUrl = "http://127.0.0.1:8081/app1-devises/api";
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target(baseUrl).path("devises").path(nom);
            Response resp = target.request(MediaType.APPLICATION_JSON_TYPE).get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(com.devises.model.Devise.class);
            }
            System.err.println("REST getByNom status: " + resp.getStatus());
            return null;
        } finally {
            client.close();
        }
    }

    @Override
    public List<Devise> getDevisesMultipliees() {
        List<Devise> result = new ArrayList<>();
        List<com.devises.model.Devise> devises;
        if (useRestMode()) {
            devises = fetchAllViaRest();
        } else {
            DeviseService remoteService = getRemoteDeviseService();
            if (remoteService == null) return result;
            devises = remoteService.getAllDevises();
        }
        for (com.devises.model.Devise d : devises) {
            Devise multiplied = new Devise(
                    d.getNomDevise(),
                    d.getDateDebut(),
                    d.getDateFin(),
                    d.getCours() * 2);
            result.add(multiplied);
        }

        return result;
    }

    @Override
    public Devise getDeviseMultipliee(String nom) {
        com.devises.model.Devise d;
        if (useRestMode()) {
            d = fetchByNomViaRest(nom);
        } else {
            DeviseService remoteService = getRemoteDeviseService();
            if (remoteService == null) return null;
            d = remoteService.getDeviseByNom(nom);
        }
        if (d != null) {
            return new Devise(
                    d.getNomDevise(),
                    d.getDateDebut(),
                    d.getDateFin(),
                    d.getCours() * 2);
        }

        return null;
    }
}