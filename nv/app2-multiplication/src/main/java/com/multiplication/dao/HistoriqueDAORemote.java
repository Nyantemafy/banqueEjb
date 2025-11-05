package com.multiplication.dao;

import com.multiplication.model.Historique;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface HistoriqueDAORemote {
    void create(Historique h);
    List<Historique> findAll();
    List<Historique> findRecent(int max);
    List<Historique> findByObjet(String objet);
}
