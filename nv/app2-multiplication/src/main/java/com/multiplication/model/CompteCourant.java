package com.multiplication.model;

import com.multiplication.metier.Virement;
import com.multiplication.metier.VirementComplet;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "compteCourant")
public class CompteCourant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compteCourant")
    private Integer idCompteCourant;

    @Column(name = "solde", precision = 15, scale = 2)
    private BigDecimal solde;

    @Column(name = "date_ouverture", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOuverture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status")
    private Status status;

    @OneToMany(mappedBy = "compteCourant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    // Constructeurs
    public CompteCourant() {
        this.solde = BigDecimal.ZERO;
        this.dateOuverture = new Date();
    }

    public CompteCourant(Utilisateur utilisateur, Status status) {
        this();
        this.utilisateur = utilisateur;
        this.status = status;
    }

    /**
     * Obtient le solde du compte
     */
    public BigDecimal getSolde() {
        return solde != null ? solde : BigDecimal.ZERO;
    }

    /**
     * Effectue un virement depuis ce compte
     * @return Objet Virement avec les contrôles effectués
     */
    public Virement virer(String compte, String compteBeneficiaire, 
                          String montant, String devise, String date) {
        // Créer le virement avec le Builder
        Virement virement = new Virement.Builder()
                .compteEmetteur(compte)
                .compteBeneficiaire(compteBeneficiaire)
                .montant(montant)
                .devise(devise)
                .dateVirement(date)
                .build();

        // Le virement contient déjà les contrôles unitaires effectués
        // Les contrôles complexes (plafond) seront effectués dans le service
        
        return virement;
    }

    // Alias demandé: vire(...)
    public Virement vire(String compte, String compteBeneficiaire,
                         String montant, String devise, String date) {
        return virer(compte, compteBeneficiaire, montant, devise, date);
    }

    /**
     * Effectue un virement complet avec infos bénéficiaire
     */
    public VirementComplet virerComplet(String compte, String compteBeneficiaire,
                                        String montant, String devise, String date) {
        // Créer d'abord le virement de base
        Virement virementBase = virer(compte, compteBeneficiaire, montant, devise, date);
        
        // Convertir en VirementComplet
        VirementComplet virementComplet = VirementComplet.fromVirement(virementBase);
        
        // Les informations du bénéficiaire seront ajoutées par le service
        return virementComplet;
    }

    // Alias demandé: virementComplet(...)
    public VirementComplet virementComplet(String compte, String compteBeneficiaire,
                                           String montant, String devise, String date) {
        return virerComplet(compte, compteBeneficiaire, montant, devise, date);
    }

    /**
     * Débite le compte
     */
    public boolean debiter(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        if (this.solde.compareTo(montant) >= 0) {
            this.solde = this.solde.subtract(montant);
            return true;
        }
        return false;
    }

    /**
     * Crédite le compte
     */
    public boolean crediter(BigDecimal montant) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        this.solde = this.solde.add(montant);
        return true;
    }

    // Getters et Setters
    public Integer getIdCompteCourant() {
        return idCompteCourant;
    }

    public void setIdCompteCourant(Integer idCompteCourant) {
        this.idCompteCourant = idCompteCourant;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    public Date getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Date dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "idCompteCourant=" + idCompteCourant +
                ", solde=" + solde +
                ", dateOuverture=" + dateOuverture +
                ", status=" + (status != null ? status.getLibelle() : "null") +
                '}';
    }
}
