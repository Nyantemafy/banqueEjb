package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Transaction;
import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Type;
import com.banque.comptecourant.remote.TransactionRemote;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless(name = "TransactionBeanCC")
public class TransactionBean implements TransactionRemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public Transaction createTransaction(Integer compteId, BigDecimal montant, Integer typeId) {
        try {
            CompteCourant compte = em.find(CompteCourant.class, compteId);
            Type type = em.find(Type.class, typeId);
            
            if (compte == null || type == null) {
                return null;
            }

            Transaction transaction = new Transaction();
            transaction.setMontant(montant);
            transaction.setDateTransaction(new Date());
            transaction.setCompteCourant(compte);
            
            // Utiliser le type fourni
            transaction.setType(type);
            
            em.persist(transaction);
            return transaction;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Transaction> getTransactionsByCompte(Integer compteId) {
        TypedQuery<Transaction> query = em.createQuery(
            "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :compteId ORDER BY t.dateTransaction DESC",
            Transaction.class);
        query.setParameter("compteId", compteId);
        return query.getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByDate(Integer compteId, Date dateDebut, Date dateFin) {
        TypedQuery<Transaction> query = em.createQuery(
            "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :compteId " +
            "AND t.dateTransaction BETWEEN :dateDebut AND :dateFin ORDER BY t.dateTransaction DESC",
            Transaction.class);
        query.setParameter("compteId", compteId);
        query.setParameter("dateDebut", dateDebut);
        query.setParameter("dateFin", dateFin);
        return query.getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByType(Integer compteId, Integer typeId) {
        TypedQuery<Transaction> query = em.createQuery(
            "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :compteId " +
            "AND t.type.idType = :typeId ORDER BY t.dateTransaction DESC",
            Transaction.class);
        query.setParameter("compteId", compteId);
        query.setParameter("typeId", typeId);
        return query.getResultList();
    }

    @Override
    public Transaction getTransactionById(Integer transactionId) {
        return em.find(Transaction.class, transactionId);
    }

    
}
