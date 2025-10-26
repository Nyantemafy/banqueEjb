package com.banque.central.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBLocator {

    private static Context context;

    static {
        try {
            // In-container lookup (no remote client properties)
            context = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException("Erreur lors de l'initialisation du contexte JNDI", e);
        }
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
        String envJndi = System.getenv("CHANGE_JNDI");
        if (envJndi != null && !envJndi.trim().isEmpty()) {
            return context.lookup(envJndi);
        }

        String[] candidates = new String[] {
                "java:global/Change-1.0-SNAPSHOT/ChangeBean!com.banque.change.remote.ChangeRemote",
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
