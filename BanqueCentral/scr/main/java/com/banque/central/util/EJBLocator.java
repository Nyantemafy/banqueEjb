package com.banque.central.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class EJBLocator {

    private static Context context;

    static {
        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            props.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
            props.put("jboss.naming.client.ejb.context", true);
            
            context = new InitialContext(props);
        } catch (NamingException e) {
            throw new RuntimeException("Erreur lors de l'initialisation du contexte JNDI", e);
        }
    }

    public static <T> T lookup(String jndiName, Class<T> clazz) throws NamingException {
        return clazz.cast(context.lookup(jndiName));
    }

    public static String buildJNDIName(String appName, String moduleName, 
                                       String beanName, String interfaceName, 
                                       boolean stateful) {
        StringBuilder sb = new StringBuilder();
        sb.append("ejb:");
        if (appName != null && !appName.isEmpty()) {
            sb.append(appName).append("/");
        }
        sb.append(moduleName).append("/");
        sb.append(beanName).append("!");
        sb.append(interfaceName);
        if (stateful) {
            sb.append("?stateful");
        }
        return sb.toString();
    }

    // Méthodes helper pour les lookups spécifiques
    public static Object lookupAuthenticationBean() throws NamingException {
        String jndiName = buildJNDIName("", "CompteCourant", "AuthenticationBean",
            "com.banque.comptecourant.remote.AuthenticationRemote", true);
        return context.lookup(jndiName);
    }

    public static Object lookupCompteBean() throws NamingException {
        String jndiName = buildJNDIName("", "CompteCourant", "CompteBean",
            "com.banque.comptecourant.remote.CompteRemote", false);
        return context.lookup(jndiName);
    }

    public static Object lookupTransactionBean() throws NamingException {
        String jndiName = buildJNDIName("", "CompteCourant", "TransactionBean",
            "com.banque.comptecourant.remote.TransactionRemote", false);
        return context.lookup(jndiName);
    }

    public static Object lookupCreditBean() throws NamingException {
        String jndiName = buildJNDIName("", "CompteCourant", "CreditBean",
            "com.banque.comptecourant.remote.CreditRemote", false);
        return context.lookup(jndiName);
    }

    public static Object lookupDepotBean() throws NamingException {
        String jndiName = buildJNDIName("", "CompteCourant", "DepotBean",
            "com.banque.comptecourant.remote.DepotRemote", false);
        return context.lookup(jndiName);
    }
}
