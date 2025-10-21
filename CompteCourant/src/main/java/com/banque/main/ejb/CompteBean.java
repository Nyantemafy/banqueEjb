package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.CompteCourant;
import com.banque.comptecourant.entity.Transaction;
import com.banque.comptecourant.entity.Type;
import com.banque.comptecourant.remote.CompteRemote;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class CompteBean implements CompteRemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public CompteCourant getCompteByUserId(Integer userId) {
        TypedQuery<CompteCourant> q = em.createQuery(
                "SELECT c FROM CompteCourant c WHERE c.utilisateur.idUser = :uid", CompteCourant.class);
        q.setParameter("uid", userId);
        List<CompteCourant> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<CompteCourant> getAllComptes() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class).getResultList();
    }

    @Override
    public BigDecimal getSolde(Integer compteId) {
        CompteCourant c = em.find(CompteCourant.class, compteId);
        return c != null ? (c.getSolde() != null ? c.getSolde() : BigDecimal.ZERO) : BigDecimal.ZERO;
    }

    @Override
    public boolean depot(Integer compteId, BigDecimal montant, String modeDepot) {
        CompteCourant c = em.find(CompteCourant.class, compteId);
        if (c == null || montant == null || montant.signum() <= 0) return false;
        BigDecimal newSolde = (c.getSolde() == null ? BigDecimal.ZERO : c.getSolde()).add(montant);
        c.setSolde(newSolde);
        em.merge(c);
        // Create transaction record (type: depot)
        Transaction t = new Transaction();
        t.setIdTransaction(generateTransactionId());
        t.setMontant(montant);
        t.setDateTransaction(new Date());
        t.setCompteCourant(c);
        // if type table used, try assign a type id for depot = 1 (or leave null if not found)
        Type type = em.find(Type.class, 1);
        if (type != null) t.setType(type);
        em.persist(t);
        return true;
    }

    @Override
    public boolean retrait(Integer compteId, BigDecimal montant, String modeRetrait) {
        CompteCourant c = em.find(CompteCourant.class, compteId);
        if (c == null || montant == null || montant.signum() <= 0) return false;
        BigDecimal solde = (c.getSolde() == null ? BigDecimal.ZERO : c.getSolde());
        if (solde.compareTo(montant) < 0) return false;
        c.setSolde(solde.subtract(montant));
        em.merge(c);
        // Create transaction record (type: retrait)
        Transaction t = new Transaction();
        t.setIdTransaction(generateTransactionId());
        t.setMontant(montant);
        t.setDateTransaction(new Date());
        t.setCompteCourant(c);
        Type type = em.find(Type.class, 2);
        if (type != null) t.setType(type);
        em.persist(t);
        return true;
    }

    @Override
    public List<Transaction> getTransactions(Integer compteId) {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :cid ORDER BY t.dateTransaction DESC",
                Transaction.class);
        q.setParameter("cid", compteId);
        return q.getResultList();
    }

    @Override
    public List<Transaction> getRecentTransactions(Integer compteId, int limit) {
        TypedQuery<Transaction> q = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.compteCourant.idCompteCourant = :cid ORDER BY t.dateTransaction DESC",
                Transaction.class);
        q.setParameter("cid", compteId);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    private Integer generateTransactionId() {
        // Simple fallback ID generation (replace with DB sequence/identity in real deployments)
        Integer maxId = em.createQuery("SELECT COALESCE(MAX(t.idTransaction), 0) FROM Transaction t", Integer.class)
                .getSingleResult();
        return maxId + 1;
    }
}
