package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "validation_virement")
public class ValidationVirement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_validation")
    private Integer idValidation;

    @Column(name = "id_object", length = 50, nullable = false, unique = true)
    private String idObject; // ex: VIR-000123

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // ex: 117 (ADMIN), 111 (AGENT_SUP), etc.

    @Column(name = "etat", length = 100, nullable = false)
    private String etat; // états cumulés ou description

    @PrePersist
    protected void onPersist() {
        if (date == null) date = new Date();
        if (status == null) status = "EN_ATTENTE";
        if (etat == null) etat = "";
    }

    @PreUpdate
    protected void onUpdate() {
        if (date == null) date = new Date();
    }

    public Integer getIdValidation() { return idValidation; }
    public void setIdValidation(Integer idValidation) { this.idValidation = idValidation; }

    public String getIdObject() { return idObject; }
    public void setIdObject(String idObject) { this.idObject = idObject; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}
