package com.multiplication.ejb;

import com.devises.ejb.DeviseService;
import com.multiplication.model.Devise;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Stateless
public class MultiplicationServiceBean implements MultiplicationService {

    private DeviseService getRemoteDeviseService() {
        try {
            Hashtable jndiProperties = new Hashtable<>();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
                    "org.wildfly.naming.client.WildFlyInitialContextFactory");
            jndiProperties.put(Context.PROVIDER_URL,
                    "remote+http://127.0.0.1:8081");
            jndiProperties.put("jboss.naming.client.ejb.context", "true");
            // Credentials of ApplicationRealm user configured in Docker WildFly
            jndiProperties.put(Context.SECURITY_PRINCIPAL, "ejbuser");
            jndiProperties.put(Context.SECURITY_CREDENTIALS, "ejbpass");
            // Force username/password over remoting and disable local-user
            jndiProperties.put("wildfly.naming.client.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
            jndiProperties.put("wildfly.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
            jndiProperties.put("wildfly.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");

            Context context = new InitialContext(jndiProperties);

            return (DeviseService) context.lookup(
                    "ejb:/app1-lecture-devises/DeviseServiceBean!com.devises.ejb.DeviseService");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Devise> getDevisesMultipliees() {
        List<Devise> result = new ArrayList<>();
        DeviseService remoteService = getRemoteDeviseService();

        if (remoteService != null) {
            List<com.devises.model.Devise> devises = remoteService.getAllDevises();
            for (com.devises.model.Devise d : devises) {
                Devise multiplied = new Devise(
                        d.getNomDevise(),
                        d.getDateDebut(),
                        d.getDateFin(),
                        d.getCours() * 2);
                result.add(multiplied);
            }
        }

        return result;
    }

    @Override
    public Devise getDeviseMultipliee(String nom) {
        DeviseService remoteService = getRemoteDeviseService();

        if (remoteService != null) {
            com.devises.model.Devise d = remoteService.getDeviseByNom(nom);
            if (d != null) {
                return new Devise(
                        d.getNomDevise(),
                        d.getDateDebut(),
                        d.getDateFin(),
                        d.getCours() * 2);
            }
        }

        return null;
    }
}