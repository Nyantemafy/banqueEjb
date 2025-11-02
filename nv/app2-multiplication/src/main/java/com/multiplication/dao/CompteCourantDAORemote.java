package com.multiplication.dao;

import com.multiplication.model.CompteCourant;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface CompteCourantDAORemote {
    void create(CompteCourant compte);
    CompteCourant findById(Integer id);
    List<CompteCourant> findByUtilisateur(Integer idUser);
    List<CompteCourant> findAll();
    void update(CompteCourant compte);
    void delete(Integer id);
    BigDecimal getMontantVirementsJour(Integer idCompte, Date date);
}
