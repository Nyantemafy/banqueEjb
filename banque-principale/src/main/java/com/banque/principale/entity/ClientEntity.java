package com.banque.principale.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @Column(name = "numero_client", length = 50, nullable = false)
    private String numeroClient;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "telephone", length = 50)
    private String telephone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_inscription", nullable = false)
    private Date dateInscription = new Date();

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "mot_de_passe", length = 255)
    private String motDePasse;

    public String getNumeroClient() { return numeroClient; }
    public void setNumeroClient(String numeroClient) { this.numeroClient = numeroClient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Date getDateInscription() { return dateInscription; }
    public void setDateInscription(Date dateInscription) { this.dateInscription = dateInscription; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
