package com.banque.central.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class DashboardData implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal soldeCourant;
    private BigDecimal soldeDepot;
    private BigDecimal montantRestantCredit;
    private BigDecimal prochaineEcheance;
    private BigDecimal totalGlobal;

    public DashboardData() {
        this.soldeCourant = BigDecimal.ZERO;
        this.soldeDepot = BigDecimal.ZERO;
        this.montantRestantCredit = BigDecimal.ZERO;
        this.prochaineEcheance = BigDecimal.ZERO;
        this.totalGlobal = BigDecimal.ZERO;
    }

    public BigDecimal getSoldeCourant() { return soldeCourant; }
    public void setSoldeCourant(BigDecimal soldeCourant) { this.soldeCourant = soldeCourant; }
    public BigDecimal getSoldeDepot() { return soldeDepot; }
    public void setSoldeDepot(BigDecimal soldeDepot) { this.soldeDepot = soldeDepot; }
    public BigDecimal getMontantRestantCredit() { return montantRestantCredit; }
    public void setMontantRestantCredit(BigDecimal montantRestantCredit) { this.montantRestantCredit = montantRestantCredit; }
    public BigDecimal getProchaineEcheance() { return prochaineEcheance; }
    public void setProchaineEcheance(BigDecimal prochaineEcheance) { this.prochaineEcheance = prochaineEcheance; }
    public BigDecimal getTotalGlobal() { return totalGlobal; }
    public void setTotalGlobal(BigDecimal totalGlobal) { this.totalGlobal = totalGlobal; }

    public void calculerTotal() {
        this.totalGlobal = this.soldeCourant.add(this.soldeDepot).subtract(this.montantRestantCredit);
    }
}