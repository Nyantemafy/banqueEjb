package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "configuration_frais_bancaire")
public class ConfigurationFraisBancaire implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frais")
    private Integer idFrais;

    @Column(name = "type_compte", length = 50, nullable = false)
    private String typeCompte;

    @Column(name = "montant_inf", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantInf;

    @Column(name = "montant_sup", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantSup;

    @Column(name = "frais_montant", precision = 15, scale = 2)
    private BigDecimal fraisMontant;

    @Column(name = "frais_pourcentage", precision = 5, scale = 2)
    private BigDecimal fraisPourcentage;

    @Column(name = "devise", length = 10)
    private String devise;

    public Integer getIdFrais() {
        return idFrais;
    }

    public void setIdFrais(Integer idFrais) {
        this.idFrais = idFrais;
    }

    public String getTypeCompte() {
        return typeCompte;
    }

    public void setTypeCompte(String typeCompte) {
        this.typeCompte = typeCompte;
    }

    public BigDecimal getMontantInf() {
        return montantInf;
    }

    public void setMontantInf(BigDecimal montantInf) {
        this.montantInf = montantInf;
    }

    public BigDecimal getMontantSup() {
        return montantSup;
    }

    public void setMontantSup(BigDecimal montantSup) {
        this.montantSup = montantSup;
    }

    public BigDecimal getFraisMontant() {
        return fraisMontant;
    }

    public void setFraisMontant(BigDecimal fraisMontant) {
        this.fraisMontant = fraisMontant;
    }

    public BigDecimal getFraisPourcentage() {
        return fraisPourcentage;
    }

    public void setFraisPourcentage(BigDecimal fraisPourcentage) {
        this.fraisPourcentage = fraisPourcentage;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }
}
