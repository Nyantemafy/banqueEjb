package com.multiplication.dao;

import com.multiplication.model.Historique;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless(name = "HistoriqueDAOApp2")
public class HistoriqueDAO implements HistoriqueDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public void create(Historique h) {
        em.persist(h);
    }

    @Override
    public List<Historique> findAll() {
        TypedQuery<Historique> q = em.createQuery(
                "SELECT h FROM Historique h ORDER BY h.dateHeure DESC", Historique.class);
        return q.getResultList();
    }

    @Override
    public List<Historique> findRecent(int max) {
        TypedQuery<Historique> q = em.createQuery(
                "SELECT h FROM Historique h ORDER BY h.dateHeure DESC", Historique.class);
        q.setMaxResults(max);
        return q.getResultList();
    }

    @Override
    public List<Historique> findByObjet(String objet) {
        TypedQuery<Historique> q = em.createQuery(
                "SELECT h FROM Historique h WHERE h.objet = :obj ORDER BY h.dateHeure DESC",
                Historique.class);
        q.setParameter("obj", objet);
        return q.getResultList();
    }
}
