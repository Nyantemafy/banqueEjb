package com.banque.comptecourant.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_transaction")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTransaction;

    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_transaction")
    @Temporal(TemporalType.DATE)
    private Date dateTransaction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_compteCourant")
    private CompteCourant compteCourant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_type")
    private Type type;

    public Transaction() {}

    public Integer getIdTransaction() { return idTransaction; }
    public void setIdTransaction(Integer idTransaction) { this.idTransaction = idTransaction; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public Date getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(Date dateTransaction) { this.dateTransaction = dateTransaction; }
    public CompteCourant getCompteCourant() { return compteCourant; }
    public void setCompteCourant(CompteCourant compteCourant) { this.compteCourant = compteCourant; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
}
