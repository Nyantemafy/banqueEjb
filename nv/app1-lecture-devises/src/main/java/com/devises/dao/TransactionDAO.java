package com.devises.dao;

import com.devises.model.Transaction;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Collections;

@Stateless
public class TransactionDAO {

    // Aucune persistance en base dans app1. Lecture/écriture via fichiers uniquement.

    public void create(Transaction transaction) {
        // Pas d'opération en base dans app1
    }

    public Transaction findById(Integer id) {
        // Non supporté côté app1 (pas de base). Les corrections renverront "introuvable".
        return null;
    }

    /**
     * Trouve les opérations liées (historique) pour un compte
     */
    public List<Transaction> findOperationsLiees(Integer idCompte) {
        return Collections.emptyList();
    }

    /**
     * Trouve les transactions en attente de validation
     */
    public List<Transaction> findEnAttente() {
        return Collections.emptyList();
    }

    /**
     * Trouve les transactions par statut
     */
    public List<Transaction> findByStatut(String statut) {
        return Collections.emptyList();
    }

    public List<Transaction> findAll() {
        return Collections.emptyList();
    }

    public void update(Transaction transaction) {
        // Pas d'opération en base dans app1
    }

    public void delete(Integer id) {
        // Pas d'opération en base dans app1
    }
}