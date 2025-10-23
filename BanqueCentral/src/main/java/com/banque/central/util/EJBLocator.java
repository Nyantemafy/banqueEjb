package com.banque.central.util;

import java.util.Arrays;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBLocator {

    private static Context localContext;
    private static Context remoteChangeContext;

    static {
        initContexts();
    }

    private static void initContexts() {
        // Context local pour CompteCourant (même serveur)
        try {
            localContext = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException("Erreur lors de l'initialisation du contexte JNDI local", e);
        }

        // Context distant pour Change (Docker)
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

            // URL du serveur Change dans Docker : prefere host.docker.internal sur Windows
            String changeUrl = getFirstNonEmpty(
                    System.getProperty("change.remote.url"),
                    System.getenv("CHANGE_REMOTE_URL"),
                    "http-remoting://host.docker.internal:8081");
            props.put(Context.PROVIDER_URL, changeUrl);

            // Credentials (si nécessaire)
            String user = getFirstNonEmpty(
                    System.getProperty("change.remote.user"),
                    System.getenv("CHANGE_REMOTE_USER"),
                    "admin");
            String pass = getFirstNonEmpty(
                    System.getProperty("change.remote.pass"),
                    System.getenv("CHANGE_REMOTE_PASS"),
                    "Admin#70365");

            if (user != null && pass != null) {
                props.put(Context.SECURITY_PRINCIPAL, user);
                props.put(Context.SECURITY_CREDENTIALS, pass);
            }

            remoteChangeContext = new InitialContext(props);
            System.out.println("Remote context for Change initialized: " + changeUrl);
        } catch (NamingException e) {
            System.err.println("Warning: Could not initialize remote context for Change EJB: " + e.getMessage());
            remoteChangeContext = null;
        }
    }

    private static String getFirstNonEmpty(String... values) {
        for (String val : values) {
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
        }
        return null;
    }

    public static <T> T lookup(String jndiName, Class<T> clazz) throws NamingException {
        return clazz.cast(localContext.lookup(jndiName));
    }

    // Méthodes pour les EJBs locaux (CompteCourant)
    public static Object lookupAuthenticationBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/AuthenticationBean!com.banque.comptecourant.remote.AuthenticationRemote";
        return localContext.lookup(jndiName);
    }

    public static Object lookupCompteBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/CompteCourantBean!com.banque.comptecourant.remote.CompteRemote";
        return localContext.lookup(jndiName);
    }

    public static Object lookupTransactionBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/TransactionBean!com.banque.comptecourant.remote.TransactionRemote";
        return localContext.lookup(jndiName);
    }

    public static Object lookupCreditBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/CreditBean!com.banque.comptecourant.remote.CreditRemote";
        return localContext.lookup(jndiName);
    }

    public static Object lookupDepotBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/DepotBean!com.banque.comptecourant.remote.DepotRemote";
        return localContext.lookup(jndiName);
    }

    public static Object lookupActionControleBean() throws NamingException {
        String jndiName = "java:global/CompteCourant/ActionControleBean!com.banque.comptecourant.ejb.ActionControleBean";
        return localContext.lookup(jndiName);
    }

    // Méthode pour l'EJB distant Change (Docker)
    public static Object lookupChangeBean() throws NamingException {
        if (remoteChangeContext == null) {
            throw new NamingException("Remote context for Change EJB not initialized");
        }

        // Essayer plusieurs noms JNDI possibles
        String[] candidates = new String[] {
                "java:global/Change/ChangeBean!com.banque.change.remote.ChangeRemote",
                "java:global/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote",
                "ejb:/Change/ChangeBean!com.banque.change.remote.ChangeRemote",
                "ejb:/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote",
                "Change/ChangeBean!com.banque.change.remote.ChangeRemote"
        };

        NamingException lastException = null;
        for (String jndi : candidates) {
            try {
                Object bean = remoteChangeContext.lookup(jndi);
                System.out.println("Successfully looked up Change EJB at: " + jndi);
                return bean;
            } catch (NamingException e) {
                lastException = e;
                System.out.println("Failed to lookup at: " + jndi + " - " + e.getMessage());
            }
        }

        throw lastException != null ? lastException
                : new NamingException("Unable to locate ChangeBean via known JNDI names");
    }

    private static Context createInitialContext() {
        if (remoteChangeContext != null) {
            return remoteChangeContext;
        }
        try {
            return new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException("Cannot create InitialContext", e);
        }
    }

    public static <T> T lookupRemote(Class<T> iface) {
        String[] candidates = new String[] {
                "ejb:/Change/ChangeBean!" + iface.getName(),
                "ejb:/Change.jar/ChangeBean!" + iface.getName(),
                "ejb:/Change-1.0-SNAPSHOT/ChangeBean!" + iface.getName(),
                "java:global/Change/ChangeBean!" + iface.getName(),
                "java:global/Change-1.0-SNAPSHOT/ChangeBean!" + iface.getName()
        };
        Context ctx = createInitialContext();
        for (String name : candidates) {
            try {
                System.out.println("Trying lookup: " + name);
                Object o = ctx.lookup(name);
                return iface.cast(o);
            } catch (Throwable t) {
                System.out.println("Failed lookup at: " + name + " -> " + t.getMessage());
            }
        }
        throw new RuntimeException(
                "Remote EJB " + iface.getName() + " not found, tried: " + Arrays.toString(candidates));
    }
}