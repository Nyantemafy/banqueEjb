package com.multiplication.dao;

import com.multiplication.model.ActionHistorique;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless(name = "ActionHistoriqueDAOApp2")
public class ActionHistoriqueDAO implements ActionHistoriqueDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public ActionHistorique findByIntitule(String intitule) {
        TypedQuery<ActionHistorique> q = em.createQuery(
                "SELECT a FROM ActionHistorique a WHERE a.intitule = :lib", ActionHistorique.class);
        q.setParameter("lib", intitule);
        java.util.List<ActionHistorique> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }
}
