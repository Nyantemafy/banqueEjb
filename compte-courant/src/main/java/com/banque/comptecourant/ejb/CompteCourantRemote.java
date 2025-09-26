package com.banque.comptecourant.ejb;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import com.banque.comptecourant.model.Transaction;

// @Remote = cette interface peut être appelée depuis d'autres applications
@Remote
public interface CompteCourantRemote {

    // Méthodes de base - SIMPLE
    BigDecimal consulterSolde(String numeroCompte);

    boolean deposer(String numeroCompte, BigDecimal montant);

    boolean retirer(String numeroCompte, BigDecimal montant);

    boolean creerCompte(String numeroCompte, String proprietaire);

    List<Transaction> getHistorique(String numeroCompte);
}