package com.multiplication.dao;

import com.multiplication.model.VirementRef;
import com.multiplication.model.Transaction;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless(name = "VirementDAOApp2")
public class VirementDAO implements VirementDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public VirementRef create(VirementRef v) {
        // Attacher la transaction au contexte courant pour garantir l'ID en FK
        if (v.getTransaction() != null && v.getTransaction().getIdTransaction() != null) {
            Integer idTx = v.getTransaction().getIdTransaction();
            Transaction managedTx = em.getReference(Transaction.class, idTx);
            v.setTransaction(managedTx);
        }
        em.persist(v);
        em.flush(); // s'assurer que l'id est généré
        em.refresh(v); // recharger code_virement (colonne générée)
        return v;
    }

    @Override
    public VirementRef findByTransactionId(Integer idTransaction) {
        TypedQuery<VirementRef> q = em.createQuery(
                "SELECT v FROM VirementRef v WHERE v.transaction.idTransaction = :id",
                VirementRef.class);
        q.setParameter("id", idTransaction);
        java.util.List<VirementRef> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }
}
