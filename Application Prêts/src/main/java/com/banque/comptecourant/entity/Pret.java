package com.banque.pret.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

@Entity
@Table(name = "prets")
@NamedQueries({
    @NamedQuery(
        name = "Pret.findByClient",
        query = "SELECT p FROM Pret p WHERE p.numeroClient = :numeroClient ORDER BY p.dateDebut DESC"
    ),
    @NamedQuery(
        name = "Pret.findActifs",
        query = "SELECT p FROM Pret p WHERE p.statutPret = 'ACTIF' ORDER BY p.dateDebut DESC"
    ),
    @NamedQuery(
        name = "Pret.findByStatut",
        query = "SELECT p FROM Pret p WHERE p.statutPret = :statut ORDER BY p.dateDebut DESC"
    ),
    @NamedQuery(
        name = "Pret.findEnRetard",
        query = "SELECT p FROM Pret p WHERE p.statutPret = 'ACTIF' AND p.joursRetard > 0 ORDER BY p.joursRetard DESC"
    )
})
public class Pret implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "numero_pret", length = 20)
    @Size(min = 5, max = 20)
    private String numeroPret;
    
    @NotNull
    @Size(min = 5, max = 20)
    @Column(name = "numero_demande", nullable = false, length = 20)
    private String numeroDemande;
    
    @NotNull
    @Size(min = 5, max = 20)
    @Column(name = "numero_client", nullable = false, length = 20)
    private String numeroClient;
    
    @NotNull
    @DecimalMin(value = "1000.00")
    @Column(name = "montant_initial", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantInitial;
    
    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "montant_restant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantRestant;
    
    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "20.0")
    @Column(name = "taux_interet", nullable = false, precision = 5, scale = 2)
    private Double tauxInteret;
    
    @NotNull
    @Min(value = 12)
    @Max(value = 360)
    @Column(name = "du# Application Prêts Java/EJB - Fichiers Complets
