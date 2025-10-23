package com.banque.central.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class EJBLocator {

    private static Context context;

    static {
        initContext();
    }

    private static void initContext() {
        // Decide between remote and in-container context
        String remoteFlag = getFirstNonEmpty(
                System.getProperty("ejb.remote"),
                System.getenv("EJB_REMOTE")
        );
        boolean useRemote = "true".equalsIgnoreCase(remoteFlag);
        if (useRemote) {
            try {
                Properties props = new Properties();
                props.put("java.naming.factory.initial", "org.wildfly.naming.client.WildFlyInitialContextFactory");
                String url = getFirstNonEmpty(System.getProperty("ejb.remote.url"), System.getenv("EJB_REMOTE_URL"));
                if (url == null || url.trim().isEmpty()) {
                    url = "http-remoting://localhost:8080"; // default
                }
                props.put("java.naming.provider.url", url);
                String user = getFirstNonEmpty(System.getProperty("ejb.remote.user"), System.getenv("EJB_REMOTE_USER"));
                String pass = getFirstNonEmpty(System.getProperty("ejb.remote.pass"), System.getenv("EJB_REMOTE_PASS"));
                if (user != null && pass != null) {
                    props.put("java.naming.security.principal", user);
                    props.put("java.naming.security.credentials", pass);
                }
                context = new InitialContext(props);
            } catch (NamingException e) {
                throw new RuntimeException("Erreur InitialContext distant: " + e.getMessage(), e);
            }
        } else {
            try {
                // In-container lookup (no remote client properties)
                context = new InitialContext();
            } catch (NamingException e) {
                throw new RuntimeException("Erreur lors de l'initialisation du contexte JNDI", e);
            }
        }
    }

    private static String getFirstNonEmpty(String a, String b){
        if (a != null && !a.trim().isEmpty()) return a;
        if (b != null && !b.trim().isEmpty()) return b;
        return null;
    }

    public static <T> T lookup(String jndiName, Class<T> clazz) throws NamingException {
        return clazz.cast(context.lookup(jndiName));
    }

    // Utiliser des noms JNDI in-container via java:global

    // Méthodes helper pour les lookups spécifiques
    public static Object lookupAuthenticationBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/AuthenticationBean!com.banque.comptecourant.remote.AuthenticationRemote";
        return context.lookup(jndiName);
    }

    public static Object lookupCompteBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/CompteCourantBean!com.banque.comptecourant.remote.CompteRemote";
        return context.lookup(jndiName);
    }

    public static Object lookupTransactionBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/TransactionBean!com.banque.comptecourant.remote.TransactionRemote";
        return context.lookup(jndiName);
    }

    public static Object lookupCreditBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/CreditBean!com.banque.comptecourant.remote.CreditRemote";
        return context.lookup(jndiName);
    }

    public static Object lookupDepotBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/DepotBean!com.banque.comptecourant.remote.DepotRemote";
        return context.lookup(jndiName);
    }

    public static Object lookupActionControleBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/ActionControleBean!com.banque.comptecourant.ejb.ActionControleBean";
        return context.lookup(jndiName);
    }

    public static Object lookupChangeBean() throws NamingException {
        String[] candidates = new String[] {
            "java:global/Change/ChangeBean!com.banque.change.remote.ChangeRemote",
            "java:global/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote",
            "java:global/change/ChangeBean!com.banque.change.remote.ChangeRemote"
        };
        NamingException last = null;
        for (String jndi : candidates) {
            try {
                return context.lookup(jndi);
            } catch (NamingException e) {
                last = e;
            }
        }
        throw (last != null) ? last : new NamingException("Unable to locate ChangeBean via known JNDI names");
    }
}
