package com.multiplication.dao;

import com.multiplication.model.Transaction;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless(name = "TransactionDAOApp2")
public class TransactionDAO implements TransactionDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    public void create(Transaction transaction) {
        em.persist(transaction);
    }

    public Transaction findById(Integer id) {
        return em.find(Transaction.class, id);
    }

    public Transaction update(Transaction transaction) {
        return em.merge(transaction);
    }

    public List<Transaction> findAll() {
        TypedQuery<Transaction> q = em.createQuery("SELECT t FROM Transaction t ORDER BY t.idTransaction DESC", Transaction.class);
        return q.getResultList();
    }

    public List<Transaction> findEnAttente() {
        TypedQuery<Transaction> q = em.createQuery("SELECT t FROM Transaction t WHERE t.statut = 'EN_ATTENTE' ORDER BY t.idTransaction DESC", Transaction.class);
        return q.getResultList();
    }

    public List<Transaction> findByCompte(Integer idCompte) {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :id ORDER BY t.idTransaction DESC",
                Transaction.class);
        q.setParameter("id", idCompte);
        return q.getResultList();
    }

    public List<Transaction> findAllVirements() {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.type.libelle = 'VIREMENT' ORDER BY t.idTransaction DESC",
                Transaction.class);
        return q.getResultList();
    }

    public List<Transaction> findVirementsEnAttente() {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.type.libelle = 'VIREMENT' AND t.statut = 'EN_ATTENTE' ORDER BY t.idTransaction DESC",
                Transaction.class);
        return q.getResultList();
    }
}
