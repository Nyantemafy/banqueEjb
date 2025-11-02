package com.multiplication.ejb;

import com.multiplication.session.SessionInfo;
import javax.ejb.Remote;

@Remote
public interface AuthenticationService {
    SessionInfo login(String username, String password);
    void logout();
    boolean isAuthenticated();
    SessionInfo getSessionInfo();
    boolean hasPermission(String action, String table);
    boolean isAdmin();
    boolean isAgent();
    boolean isClient();
}
