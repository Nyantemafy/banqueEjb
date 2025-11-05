package com.multiplication.dao;

import com.multiplication.model.ActionHistorique;
import javax.ejb.Remote;

@Remote
public interface ActionHistoriqueDAORemote {
    ActionHistorique findByIntitule(String intitule);
}
