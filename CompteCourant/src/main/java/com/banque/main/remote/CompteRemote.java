package com.banque.comptecourant.remote;

import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Transaction;
import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;

@Remote
public interface CompteRemote {
    CompteCourant getCompteByUserId(Integer userId);
    List<CompteCourant> getAllComptes();
    BigDecimal getSolde(Integer compteId);
    boolean depot(Integer compteId, BigDecimal montant, String modeDepot);
    boolean retrait(Integer compteId, BigDecimal montant, String modeRetrait);
    List<Transaction> getTransactions(Integer compteId);
    List<Transaction> getRecentTransactions(Integer compteId, int limit);
}