package com.multiplication.dao;

import com.multiplication.model.ConfigurationFraisBancaire;

import javax.ejb.Remote;
import java.math.BigDecimal;

@Remote
public interface ConfigurationFraisDAORemote {
    ConfigurationFraisBancaire findApplicable(String typeCompte, String devise, BigDecimal montant);
    BigDecimal computeFrais(String typeCompte, String devise, BigDecimal montant);
}
