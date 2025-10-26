package com.banque.comptecourant.remote;

import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Transaction;
import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Date;

@Remote
public interface CompteRemote {
    CompteCourant getCompteByUserId(Integer userId);

    List<CompteCourant> getAllComptes();

    BigDecimal getSolde(Integer compteId);

    String getEtat(Integer compteId);

    boolean depot(Integer compteId, BigDecimal montant, String modeDepot);

    boolean retrait(Integer compteId, BigDecimal montant, String modeRetrait);

    boolean depotAvecDate(Integer compteId, BigDecimal montant, String modeDepot, Date date);

    boolean retraitAvecDate(Integer compteId, BigDecimal montant, String modeRetrait, Date date);

    List<Transaction> getTransactions(Integer compteId);

    List<Transaction> getRecentTransactions(Integer compteId, int limit);

    Integer createUtilisateurEtCompte(String username,
            String password,
            Integer idRole,
            Integer idDirection,
            Integer idStatus,
            BigDecimal soldeInitial);

    List<Map<String, Object>> getTransactionsWithCurrency(Integer compteId, String targetCurrency);

    List<String> getAvailableCurrencies();

    String getDefaultCurrency();
}