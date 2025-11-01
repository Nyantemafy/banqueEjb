package com.banque.comptecourant.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.banque.change.remote.ChangeRemote;

public class ChangeUtil {
    private static ChangeRemote changeBean;

    public static ChangeRemote getChangeBean() throws NamingException {
        if (changeBean == null) {
            Hashtable<String, String> props = new Hashtable<>();
            props.put(Context.INITIAL_CONTEXT_FACTORY,
                    "org.wildfly.naming.client.WildFlyInitialContextFactory");

            // Lecture configuration via env ou propriétés système, avec défauts Docker
            String host = System.getenv().getOrDefault("CHANGE_HOST",
                    System.getProperty("change.host", "change"));
            String port = System.getenv().getOrDefault("CHANGE_PORT",
                    System.getProperty("change.port", "8080"));
            String protocol = System.getenv().getOrDefault("CHANGE_PROTOCOL",
                    System.getProperty("change.protocol", "remote+http"));
            String user = System.getenv().getOrDefault("CHANGE_USER",
                    System.getProperty("change.user", "ejbuser"));
            String pass = System.getenv().getOrDefault("CHANGE_PASS",
                    System.getProperty("change.pass", "ejbpass"));

            String providerUrl = protocol + "://" + host + ":" + port;

            props.put(Context.PROVIDER_URL, providerUrl);
            props.put(Context.SECURITY_PRINCIPAL, user);
            props.put(Context.SECURITY_CREDENTIALS, pass);
            // Préférer PLAIN côté client, tout en permettant la négociation
            props.remove(Context.SECURITY_AUTHENTICATION);
            props.put("jboss.naming.client.ejb.context", "true");

            // Désactiver JBOSS-LOCAL-USER explicitement et interdire l'anonyme (Elytron)
            props.put("wildfly.sasl.disallowed-mechanisms", "JBOSS-LOCAL-USER");
            props.put("wildfly.sasl.relax-compliance", "true");
            props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
            props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "true");
            // Autoriser PLAIN (nécessaire si DIGEST-MD5 est indisponible côté serveur)
            props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
            // Préférer PLAIN puis DIGEST-MD5
            props.put("wildfly.sasl.mechanism-selector", "PLAIN,DIGEST-MD5");
            // Indiquer explicitement le realm SASL attendu côté serveur (ApplicationRealm par défaut)
            props.put("wildfly.sasl.server-realm", "ApplicationRealm");

            Context remoteContext = new InitialContext(props);
            String module = System.getenv().getOrDefault("CHANGE_MODULE",
                    System.getProperty("change.module", "Change-1.0-SNAPSHOT"));
            String jndi = "ejb:/" + module + "/ChangeBean!com.banque.change.remote.ChangeRemote";
            changeBean = (ChangeRemote) remoteContext.lookup(jndi);
        }
        return changeBean;
    }

    public static boolean isChangeAvailable() {
        try {
            ChangeRemote bean = getChangeBean();
            bean.getDefaultCurrency();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}