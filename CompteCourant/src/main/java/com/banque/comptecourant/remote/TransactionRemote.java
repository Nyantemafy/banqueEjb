package com.banque.comptecourant.remote;

import com.banque.comptecourant.entity.Transaction;
import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Remote
public interface TransactionRemote {
    Transaction createTransaction(Integer compteId, BigDecimal montant, Integer typeId);
    List<Transaction> getTransactionsByCompte(Integer compteId);
    List<Transaction> getTransactionsByDate(Integer compteId, Date dateDebut, Date dateFin);
    List<Transaction> getTransactionsByType(Integer compteId, Integer typeId);
    Transaction getTransactionById(Integer transactionId);
}