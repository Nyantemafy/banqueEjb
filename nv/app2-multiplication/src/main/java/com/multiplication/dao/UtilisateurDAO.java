package com.multiplication.dao;

import com.multiplication.model.Utilisateur;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class UtilisateurDAO {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    public void create(Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    public Utilisateur findById(Integer id) {
        return em.find(Utilisateur.class, id);
    }

    public Utilisateur findByUsername(String username) {
        TypedQuery<Utilisateur> query = em.createQuery(
                "SELECT u FROM Utilisateur u WHERE u.username = :username",
                Utilisateur.class);
        query.setParameter("username", username);

        List<Utilisateur> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Utilisateur> findAll() {
        return em.createQuery("SELECT u FROM Utilisateur u", Utilisateur.class)
                .getResultList();
    }

    public void update(Utilisateur utilisateur) {
        em.merge(utilisateur);
    }

    public void delete(Integer id) {
        Utilisateur utilisateur = findById(id);
        if (utilisateur != null) {
            em.remove(utilisateur);
        }
    }

    /**
     * Authentifie un utilisateur
     */
    public Utilisateur authentifier(String username, String password) {
        Utilisateur user = findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // Charger eagerly les relations pour la session
            user.getActionRole();
            user.getDirection();
            user.getStatus();
            return user;
        }
        return null;
    }
}