package com.banque.central.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.banque.change.remote.ChangeRemote;

public class EJBLocator {

    private static Context localContext;

    static {
        try {
            localContext = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException("Erreur lors de l'initialisation du contexte JNDI local", e);
        }
    }

    private static <T> T lookupLocal(String jndi, Class<T> clazz) throws NamingException {
        Object o = localContext.lookup(jndi);
        return clazz.cast(o);
    }

    public static Object lookupAuthenticationBean() throws NamingException {
        return lookupLocal(
                "java:global/CompteCourant/AuthenticationBean!com.banque.comptecourant.remote.AuthenticationRemote",
                Object.class);
    }

    public static Object lookupCompteBean() throws NamingException {
        return lookupLocal(
                "java:global/CompteCourant/CompteCourantBean!com.banque.comptecourant.remote.CompteRemote",
                Object.class);
    }

    public static Object lookupCreditBean() throws NamingException {
        return lookupLocal(
                "java:global/CompteCourant/CreditBean!com.banque.comptecourant.remote.CreditRemote",
                Object.class);
    }

    public static Object lookupDepotBean() throws NamingException {
        return lookupLocal(
                "java:global/CompteCourant/DepotBean!com.banque.comptecourant.remote.DepotRemote",
                Object.class);
    }

    // Lookup distant via JNDI HTTP
    public static ChangeRemote lookupChangeBean() throws NamingException {
        java.util.Properties props = new java.util.Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, "http-remoting://localhost:8081");
        props.put(Context.SECURITY_PRINCIPAL, "ejbuser");
        props.put(Context.SECURITY_CREDENTIALS, "ejbpass");
        props.put("jboss.naming.client.ejb.context", "true");

        Context ctx = new InitialContext(props);
        return (ChangeRemote) ctx.lookup(
                "ejb:/Change/ChangeBean!com.banque.change.remote.ChangeRemote");

    }

}
