package com.banque.central.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Localisateur simplifié - tous les EJBs sont locaux
 */
public class EJBLocator {

    private static Context context;

    static {
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException("Erreur lors de l'initialisation du contexte JNDI", e);
        }
    }

    public static Object lookupAuthenticationBean() throws NamingException {
        return context.lookup(
                "java:global/CompteCourant/AuthenticationBean!com.banque.comptecourant.remote.AuthenticationRemote");
    }

    public static Object lookupCompteBean() throws NamingException {
        return context.lookup(
                "java:global/CompteCourant/CompteCourantBean!com.banque.comptecourant.remote.CompteRemote");
    }

    public static Object lookupTransactionBean() throws NamingException {
        return context.lookup(
                "java:global/CompteCourant/TransactionBean!com.banque.comptecourant.remote.TransactionRemote");
    }

    public static Object lookupCreditBean() throws NamingException {
        return context.lookup("java:global/CompteCourant/CreditBean!com.banque.comptecourant.remote.CreditRemote");
    }

    public static Object lookupDepotBean() throws NamingException {
        return context.lookup("java:global/CompteCourant/DepotBean!com.banque.comptecourant.remote.DepotRemote");
    }
}