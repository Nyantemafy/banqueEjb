package com.banque.comptecourant.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "transactions_cc")
@NamedQueries({
        @NamedQuery(name = "Transaction.findByAccount", query = "SELECT t FROM Transaction t WHERE t.numeroCompte = :numeroCompte ORDER BY t.dateTransaction DESC"),
        @NamedQuery(name = "Transaction.findByAccountAndDateRange", query = "SELECT t FROM Transaction t WHERE t.numeroCompte = :numeroCompte AND t.dateTransaction BETWEEN :dateDebut AND :dateFin ORDER BY t.dateTransaction DESC"),
        @NamedQuery(name = "Transaction.findByType", query = "SELECT t FROM Transaction t WHERE t.numeroCompte = :numeroCompte AND t.type = :type ORDER BY t.dateTransaction DESC"),
        @NamedQuery(name = "Transaction.calculateSum", query = "SELECT SUM(t.montant) FROM Transaction t WHERE t.numeroCompte = :numeroCompte AND t.dateTransaction BETWEEN :dateDebut AND :dateFin")
})
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long idTransaction;

    @NotNull(message = "Le numéro de compte ne peut pas être null")
    @Size(min = 5, max = 20)
    @Column(name = "numero_compte", nullable = false, length = 20)
    private String numeroCompte;

    @NotNull(message = "Le montant ne peut pas être null")
    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @NotNull(message = "Le type de transaction ne peut pas être null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TypeTransaction type;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "date_transaction", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTransaction;

    @Column(name = "solde_apres_transaction", precision = 15, scale = 2)
    private BigDecimal soldeApresTransaction;

    @Size(max = 50)
    @Column(name = "reference_externe", length = 50)
    private String referenceExterne;

    @Column(name = "frais", precision = 10, scale = 2)
    private BigDecimal frais = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", length = 20)
    private StatutTransaction statut = StatutTransaction.VALIDEE;

    // Énumérations
    public enum TypeTransaction {
        DEPOT("Dépôt"),
        RETRAIT("Retrait"),
        VIREMENT_ENTRANT("Virement entrant"),
        VIREMENT_SORTANT("Virement sortant"),
        PRELEVEMENT("Prélèvement"),
        FRAIS("Frais bancaires"),
        INTERETS("Intérêts");

        private final String libelle;

        TypeTransaction(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutTransaction {
        EN_ATTENTE("En attente"),
        VALIDEE("Validée"),
        REJETEE("Rejetée"),
        ANNULEE("Annulée");

        private final String libelle;

        StatutTransaction(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    // Constructeurs
    public Transaction() {
        this.dateTransaction = new Date();
        this.statut = StatutTransaction.VALIDEE;
        this.frais = BigDecimal.ZERO;
    }

    public Transaction(String numeroCompte, BigDecimal montant, TypeTransaction type, String description) {
        this();
        this.numeroCompte = numeroCompte;
        this.montant = montant;
        this.type = type;
        this.description = description;
    }

    // Getters et Setters
    public Long getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(Long idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public TypeTransaction getType() {
        return type;
    }

    public void setType(TypeTransaction type) {
        this.type = type;
    }

    // Méthode de compatibilité pour les anciens codes
    public void setType(String typeString) {
        if ("DEPOT".equals(typeString)) {
            this.type = TypeTransaction.DEPOT;
        } else if ("RETRAIT".equals(typeString)) {
            this.type = TypeTransaction.RETRAIT;
        } else {
            this.type = TypeTransaction.valueOf(typeString);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public BigDecimal getSoldeApresTransaction() {
        return soldeApresTransaction;
    }

    public void setSoldeApresTransaction(BigDecimal soldeApresTransaction) {
        this.soldeApresTransaction = soldeApresTransaction;
    }

    public String getReferenceExterne() {
        return referenceExterne;
    }

    public void setReferenceExterne(String referenceExterne) {
        this.referenceExterne = referenceExterne;
    }

    public BigDecimal getFrais() {
        return frais;
    }

    public void setFrais(BigDecimal frais) {
        this.frais = frais;
    }

    public StatutTransaction getStatut() {
        return statut;
    }

    public void setStatut(StatutTransaction statut) {
        this.statut = statut;
    }

    // Méthodes utilitaires
    public boolean estCredit() {
        return montant != null && montant.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean estDebit() {
        return montant != null && montant.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getMontantAbsolu() {
        return montant != null ? montant.abs() : BigDecimal.ZERO;
    }

    public String getTypeLibelle() {
        return type != null ? type.getLibelle() : "";
    }

    public String getStatutLibelle() {
        return statut != null ? statut.getLibelle() : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Transaction that = (Transaction) obj;
        return idTransaction != null ? idTransaction.equals(that.idTransaction) : that.idTransaction == null;
    }

    @Override
    public int hashCode() {
        return idTransaction != null ? idTransaction.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", numeroCompte='" + numeroCompte + '\'' +
                ", montant=" + montant +
                ", type=" + type +
                ", dateTransaction=" + dateTransaction +
                ", statut=" + statut +
                '}';
    }
}