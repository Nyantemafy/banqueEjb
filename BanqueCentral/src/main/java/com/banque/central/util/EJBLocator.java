package com.banque.central.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBLocator {

    private static Context context;

    static {
        try {
            // 1️⃣ Tente d'abord un lookup in-container
            context = new InitialContext();
        } catch (NamingException e) {
            // Si on est hors container, fallback sur remote via HTTP remoting
            try {
                Hashtable<String, String> props = new Hashtable<>();
                props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
                // URL du container WildFly (Docker)
                props.put(Context.PROVIDER_URL,
                        System.getenv().getOrDefault("WILDFLY_URL", "http-remoting://localhost:8081"));
                // Optionnel : utilisateur/mot de passe si nécessaire
                String user = System.getenv("WILDFLY_USER");
                String pass = System.getenv("WILDFLY_PASS");
                if (user != null && pass != null) {
                    props.put(Context.SECURITY_PRINCIPAL, user);
                    props.put(Context.SECURITY_CREDENTIALS, pass);
                }
                context = new InitialContext(props);
            } catch (NamingException ex) {
                throw new RuntimeException("Impossible d'initialiser le contexte JNDI (in-container ou remote)", ex);
            }
        }
    }

    public static <T> T lookup(String jndiName, Class<T> clazz) throws NamingException {
        return clazz.cast(context.lookup(jndiName));
    }

    // Méthodes helper existantes pour EJBs
    public static Object lookupAuthenticationBean() throws NamingException {
        return context.lookup(
                "java:global/CompteCourant/AuthenticationBean!com.banque.comptecourant.remote.AuthenticationRemote");
    }

    public static Object lookupCompteBean() throws NamingException {
        return context
                .lookup("java:global/CompteCourant/CompteCourantBean!com.banque.comptecourant.remote.CompteRemote");
    }

    public static Object lookupTransactionBean() throws NamingException {
        return context
                .lookup("java:global/CompteCourant/TransactionBean!com.banque.comptecourant.remote.TransactionRemote");
    }

    public static Object lookupCreditBean() throws NamingException {
        return context.lookup("java:global/CompteCourant/CreditBean!com.banque.comptecourant.remote.CreditRemote");
    }

    public static Object lookupDepotBean() throws NamingException {
        return context.lookup("java:global/CompteCourant/DepotBean!com.banque.comptecourant.remote.DepotRemote");
    }

    public static Object lookupActionControleBean() throws NamingException {
        return context
                .lookup("java:global/CompteCourant/ActionControleBean!com.banque.comptecourant.ejb.ActionControleBean");
    }

    public static Object lookupChangeBean() throws NamingException {
        // 1️⃣ Utilise la variable d'environnement Docker si définie
        String envJndi = System.getenv("CHANGE_JNDI");
        if (envJndi != null && !envJndi.trim().isEmpty()) {
            try {
                return context.lookup(envJndi);
            } catch (NamingException e) {
                System.err.println("Echec du lookup via CHANGE_JNDI=" + envJndi + " : " + e.getMessage());
            }
        }

        // 2️⃣ Fallback sur les noms JNDI connus
        String[] candidates = new String[] {
                "java:global/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote",
                "java:global/Change/ChangeBean!com.banque.change.remote.ChangeRemote",
                "java:app/ChangeBean!com.banque.change.remote.ChangeRemote",
                "java:module/ChangeBean!com.banque.change.remote.ChangeRemote"
        };

        NamingException last = null;
        for (String jndi : candidates) {
            try {
                return context.lookup(jndi);
            } catch (NamingException e) {
                last = e;
            }
        }

        throw (last != null) ? last
                : new NamingException("Unable to locate ChangeBean via known JNDI names or CHANGE_JNDI env variable");
    }
}
