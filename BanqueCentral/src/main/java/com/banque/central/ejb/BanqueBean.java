package com.banque.central.ejb;

import com.banque.central.util.EJBLocator;

import javax.ejb.Stateless;
import javax.naming.NamingException;

@Stateless
public class BanqueBean {

    public Object getAuthenticationBean() {
        try {
            return EJBLocator.lookupAuthenticationBean();
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getCompteBean() {
        try {
            return EJBLocator.lookupCompteBean();
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getCreditBean() {
        try {
            return EJBLocator.lookupCreditBean();
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getDepotBean() {
        try {
            return EJBLocator.lookupDepotBean();
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }
}