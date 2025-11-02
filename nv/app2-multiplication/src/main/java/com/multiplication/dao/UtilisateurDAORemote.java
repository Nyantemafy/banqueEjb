package com.multiplication.dao;

import com.multiplication.model.Utilisateur;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface UtilisateurDAORemote {
    void create(Utilisateur utilisateur);
    Utilisateur findById(Integer id);
    Utilisateur findByUsername(String username);
    List<Utilisateur> findAll();
    void update(Utilisateur utilisateur);
    void delete(Integer id);
    Utilisateur authentifier(String username, String password);
}
