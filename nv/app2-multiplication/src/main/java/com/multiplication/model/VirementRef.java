package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "virement")
public class VirementRef implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_virement")
    private Integer idVirement;

    @Column(name = "code_virement", length = 20, insertable = false, updatable = false)
    private String codeVirement;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_transaction", unique = true, nullable = false)
    private Transaction transaction;

    public Integer getIdVirement() {
        return idVirement;
    }

    public void setIdVirement(Integer idVirement) {
        this.idVirement = idVirement;
    }

    public String getCodeVirement() {
        return codeVirement;
    }

    public void setCodeVirement(String codeVirement) {
        this.codeVirement = codeVirement;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
