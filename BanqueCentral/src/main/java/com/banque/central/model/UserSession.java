package com.banque.central.model;

import java.io.Serializable;

public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String username;
    private String role;
    private boolean authenticated;

    public UserSession() {}

    public UserSession(Integer userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.authenticated = true;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
}