package com.banque.comptecourant.ejb;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import com.banque.comptecourant.model.Transaction;

@Remote
public interface CompteCourantRemote {
    // Opérations de base
    BigDecimal consulterSolde(String numeroCompte);

    boolean deposer(String numeroCompte, BigDecimal montant, String description);

    boolean retirer(String numeroCompte, BigDecimal montant, String description);

    // Historique
    List<Transaction> getHistoriqueTransactions(String numeroCompte);

    // Gestion compte
    boolean creerCompte(String numeroCompte, String proprietaire);

    boolean compteExiste(String numeroCompte);
}