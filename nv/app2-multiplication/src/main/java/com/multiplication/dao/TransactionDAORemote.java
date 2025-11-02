package com.multiplication.dao;

import com.multiplication.model.Transaction;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface TransactionDAORemote {
    void create(Transaction transaction);
    Transaction findById(Integer id);
    Transaction update(Transaction transaction);
    List<Transaction> findAll();
    List<Transaction> findEnAttente();
    List<Transaction> findByCompte(Integer idCompte);
}
