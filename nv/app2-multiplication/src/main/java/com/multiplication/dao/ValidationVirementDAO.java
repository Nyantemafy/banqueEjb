package com.multiplication.dao;

import com.multiplication.model.ValidationVirement;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;

@Stateless(name = "ValidationVirementDAOApp2")
public class ValidationVirementDAO implements ValidationVirementDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public ValidationVirement findByIdObject(String idObject) {
        TypedQuery<ValidationVirement> q = em.createQuery(
                "SELECT v FROM ValidationVirement v WHERE v.idObject = :o", ValidationVirement.class);
        q.setParameter("o", idObject);
        java.util.List<ValidationVirement> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ValidationVirement upsertValidation(String idObject, String roleLibelle) {
        ValidationVirement vv = findByIdObject(idObject);
        boolean created = false;
        if (vv == null) {
            vv = new ValidationVirement();
            vv.setIdObject(idObject);
            vv.setStatus("EN_ATTENTE");
            vv.setEtat("");
            vv.setDate(new Date());
            em.persist(vv);
            em.flush();
            created = true;
        }

        // Mapping du status par rôle (exigence: ADMIN->117, AGENT_SUP->111)
        String r = roleLibelle != null ? roleLibelle.toUpperCase() : "";
        if ("ADMIN".equals(r)) {
            vv.setStatus("117");
        } else if ("AGENT_SUP".equals(r)) {
            vv.setStatus("111");
        } else if ("AGENT".equals(r)) {
            vv.setStatus("101"); // par défaut pour AGENT (à ajuster si nécessaire)
        }

        // Mettre à jour etat en ajoutant le rôle courant (sans dupliquer visuellement)
        String etat = vv.getEtat() == null ? "" : vv.getEtat();
        if (etat.isEmpty()) etat = r;
        else if (!etat.contains(r)) etat = etat + "," + r;
        vv.setEtat(etat);
        vv.setDate(new Date());

        vv = created ? vv : em.merge(vv);
        return vv;
    }

    @Override
    public void markAnnule(String idObject) {
        ValidationVirement vv = findByIdObject(idObject);
        if (vv != null) {
            vv.setStatus("ANNULE");
            vv.setDate(new Date());
            em.merge(vv);
        }
    }

    @Override
    public java.util.List<ValidationVirement> listAll() {
        return em.createQuery("SELECT v FROM ValidationVirement v ORDER BY v.date DESC", ValidationVirement.class)
                .getResultList();
    }
}
