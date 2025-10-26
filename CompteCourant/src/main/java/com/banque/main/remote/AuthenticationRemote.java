package com.banque.comptecourant.remote;

import com.banque.comptecourant.entity.Direction;
import com.banque.comptecourant.entity.Action;
import com.banque.comptecourant.entity.Utilisateur;
import javax.ejb.Remote;

@Remote
public interface AuthenticationRemote {
    Utilisateur authenticate(String username, String password);
    Direction[] getUserDirections(Integer userId);
    Action[] getUserActions(Integer userId);
    String[] getUserAuthorizedTables(Integer userId);
    void logout();
    boolean isAuthenticated();
}