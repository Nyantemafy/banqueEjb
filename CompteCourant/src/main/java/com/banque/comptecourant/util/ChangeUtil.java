package com.banque.comptecourant.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.banque.change.remote.ChangeRemote;

public class ChangeUtil {

    private static ChangeRemote changeBean;

    /**
     * Récupère le bean Change depuis Docker (lazy loading)
     */
    public static ChangeRemote getChangeBean() throws NamingException {
        if (changeBean == null) {
            // Configuration EJB remote vers Docker
            Hashtable<String, String> props = new Hashtable<>();
            props.put(Context.INITIAL_CONTEXT_FACTORY,
                    "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "remote+http://localhost:8081");
            props.put(Context.SECURITY_PRINCIPAL, "ejbuser");
            props.put(Context.SECURITY_CREDENTIALS, "ejbpass");
            props.put("jboss.naming.client.ejb.context", "true");

            Context remoteContext = new InitialContext(props);

            // Lookup du bean Change
            String jndi = "ejb:/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote";
            changeBean = (ChangeRemote) remoteContext.lookup(jndi);
        }
        return changeBean;
    }

    /**
     * Vérifie si Change est disponible
     */
    public static boolean isChangeAvailable() {
        try {
            ChangeRemote bean = getChangeBean();
            bean.getDefaultCurrency(); // Test simple
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}