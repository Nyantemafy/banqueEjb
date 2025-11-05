package com.multiplication.dao;

import com.multiplication.model.ConfigurationFraisBancaire;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;

@Stateless(name = "ConfigurationFraisDAOApp2")
public class ConfigurationFraisDAO implements ConfigurationFraisDAORemote {

    @PersistenceContext(unitName = "BanquePU")
    private EntityManager em;

    @Override
    public ConfigurationFraisBancaire findApplicable(String typeCompte, String devise, BigDecimal montant) {
        TypedQuery<ConfigurationFraisBancaire> q = em.createQuery(
                "SELECT c FROM ConfigurationFraisBancaire c " +
                        "WHERE c.typeCompte = :type AND (c.devise = :devise OR c.devise IS NULL) " +
                        "AND c.montantInf <= :m AND c.montantSup >= :m " +
                        "ORDER BY c.montantInf DESC", ConfigurationFraisBancaire.class);
        q.setParameter("type", typeCompte);
        q.setParameter("devise", devise);
        q.setParameter("m", montant);
        q.setMaxResults(1);
        java.util.List<ConfigurationFraisBancaire> list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public BigDecimal computeFrais(String typeCompte, String devise, BigDecimal montant) {
        ConfigurationFraisBancaire cfg = findApplicable(typeCompte, devise, montant);
        if (cfg == null) return BigDecimal.ZERO;
        BigDecimal fixe = cfg.getFraisMontant() == null ? BigDecimal.ZERO : cfg.getFraisMontant();
        BigDecimal prc = cfg.getFraisPourcentage() == null ? BigDecimal.ZERO : cfg.getFraisPourcentage();
        BigDecimal var = montant.multiply(prc).divide(new BigDecimal("100"));
        return fixe.add(var);
    }
}
