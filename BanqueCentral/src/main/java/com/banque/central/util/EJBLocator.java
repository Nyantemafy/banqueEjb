package com.banque.central.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBLocator {

    private static Context localContext;
    private static Context remoteContext;

    static {
        try {
            // ------- CONTEXTE LOCAL (CompteCourant) -------
            localContext = new InitialContext();

            // ------- CONTEXTE REMOTE (Change dans Docker) -------
            Hashtable<String, String> props = new Hashtable<>();
            props.put(Context.INITIAL_CONTEXT_FACTORY,
                    "org.wildfly.naming.client.WildFlyInitialContextFactory");

            // IP du Docker / port d'écoute http-remoting
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");

            // Authentification du serveur EJB distant (Docker)
            props.put(Context.SECURITY_PRINCIPAL, "ejbuser"); // <--- le user que tu as ajouté
            props.put(Context.SECURITY_CREDENTIALS, "ejbpass"); // <--- son password

            // Indique qu’on fait du lookup EJB
            props.put("jboss.naming.client.ejb.context", "true");

            remoteContext = new InitialContext(props);

        } catch (NamingException e) {
            throw new RuntimeException("Erreur lors de l'initialisation des contextes JNDI", e);
        }
    }

    // ===== EJB CompteCourant (LOCAL) =====
    public static Object lookupAuthenticationBean() throws NamingException {
        return localContext.lookup(
                "java:global/CompteCourant/AuthenticationBean!com.banque.comptecourant.remote.AuthenticationRemote");
    }

    public static Object lookupCompteBean() throws NamingException {
        return localContext.lookup(
                "java:global/CompteCourant/CompteCourantBean!com.banque.comptecourant.remote.CompteRemote");
    }

    public static Object lookupTransactionBean() throws NamingException {
        return localContext.lookup(
                "java:global/CompteCourant/TransactionBean!com.banque.comptecourant.remote.TransactionRemote");
    }

    public static Object lookupCreditBean() throws NamingException {
        return localContext.lookup(
                "java:global/CompteCourant/CreditBean!com.banque.comptecourant.remote.CreditRemote");
    }

    public static Object lookupDepotBean() throws NamingException {
        return localContext.lookup(
                "java:global/CompteCourant/DepotBean!com.banque.comptecourant.remote.DepotRemote");
    }

    // ===== EJB Change (REMOTE DOCKER) =====
    public static Object lookupChangeBean() throws NamingException {
        String jndi = "ejb:/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote";
        return remoteContext.lookup(jndi);
    }
}
