package com.multiplication.dao;

import com.multiplication.model.CompteCourant;
import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless(name = "CompteCourantDAOApp2")
@Remote(CompteCourantDAORemote.class)
public class CompteCourantDAO implements CompteCourantDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    public void create(CompteCourant compte) {
        em.persist(compte);
    }

    public CompteCourant findById(Integer id) {
        return em.find(CompteCourant.class, id);
    }

    public List<CompteCourant> findByUtilisateur(Integer idUser) {
        TypedQuery<CompteCourant> query = em.createQuery(
                "SELECT c FROM CompteCourant c WHERE c.utilisateur.idUser = :idUser",
                CompteCourant.class);
        query.setParameter("idUser", idUser);
        return query.getResultList();
    }

    public List<CompteCourant> findAll() {
        return em.createQuery("SELECT c FROM CompteCourant c", CompteCourant.class)
                .getResultList();
    }

    public void update(CompteCourant compte) {
        em.merge(compte);
    }

    public void delete(Integer id) {
        CompteCourant compte = findById(id);
        if (compte != null) {
            em.remove(compte);
        }
    }

    /**
     * Calcule le montant total des virements effectués aujourd'hui par un compte
     */
    public BigDecimal getMontantVirementsJour(Integer idCompte, Date date) {
        TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t " +
                        "WHERE t.compteCourant.idCompteCourant = :idCompte " +
                        "AND t.type.libelle = 'VIREMENT' " +
                        "AND t.dateTransaction = :date " +
                        "AND t.statut != 'ANNULE'",
                BigDecimal.class);
        query.setParameter("idCompte", idCompte);
        query.setParameter("date", date);

        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
