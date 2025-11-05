package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "mouvement")
public class Type implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type")
    private Integer idType;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "source", length = 100)
    private String source;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    // Constructeurs
    public Type() {
    }

    public Type(String libelle) {
        this.libelle = libelle;
    }

    // Getters et Setters
    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Type{" +
                "idType=" + idType +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
