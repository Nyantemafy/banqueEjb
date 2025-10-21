package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Transaction;
import com.banque.comptecourant.entity.Type;
import com.banque.comptecourant.remote.TransactionRemote;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class TransactionBean implements TransactionRemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public Transaction createTransaction(Integer compteId, BigDecimal montant, Integer typeId) {
        CompteCourant c = em.find(CompteCourant.class, compteId);
        if (c == null) return null;
        Transaction t = new Transaction();
        Integer nextId = em.createQuery("SELECT COALESCE(MAX(t.idTransaction), 0) FROM Transaction t", Integer.class)
                .getSingleResult() + 1;
        t.setIdTransaction(nextId);
        t.setMontant(montant);
        t.setDateTransaction(new Date());
        t.setCompteCourant(c);
        if (typeId != null) {
            Type type = em.find(Type.class, typeId);
            if (type != null) t.setType(type);
        }
        em.persist(t);
        return t;
    }

    @Override
    public List<Transaction> getTransactionsByCompte(Integer compteId) {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :cid ORDER BY t.dateTransaction DESC",
                Transaction.class);
        q.setParameter("cid", compteId);
        return q.getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByDate(Integer compteId, Date dateDebut, Date dateFin) {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :cid AND t.dateTransaction BETWEEN :d1 AND :d2 ORDER BY t.dateTransaction DESC",
                Transaction.class);
        q.setParameter("cid", compteId);
        q.setParameter("d1", dateDebut, TemporalType.DATE);
        q.setParameter("d2", dateFin, TemporalType.DATE);
        return q.getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByType(Integer compteId, Integer typeId) {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :cid AND t.type.idType = :tid ORDER BY t.dateTransaction DESC",
                Transaction.class);
        q.setParameter("cid", compteId);
        q.setParameter("tid", typeId);
        return q.getResultList();
    }

    @Override
    public Transaction getTransactionById(Integer transactionId) {
        return em.find(Transaction.class, transactionId);
    }
}
