package com.multiplication.metier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe métier représentant un virement avec contrôles unitaires et complexes
 */
public class Virement implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final BigDecimal PLAFOND_JOURNALIER = new BigDecimal("10000000.00"); // 10.000.000 AR

    private String compteEmetteur;
    private String compteBeneficiaire;
    private BigDecimal montant;
    private String devise;
    private Date dateVirement;
    private String statut;
    private List<String> erreurs;

    // Constructeur protégé - utiliser le Builder (et autoriser l'héritage)
    protected Virement() {
        this.erreurs = new ArrayList<>();
        this.statut = "EN_ATTENTE";
    }

    /**
     * Builder pattern pour créer un virement à partir des String de l'interface
     */
    public static class Builder {
        private String compteEmetteur;
        private String compteBeneficiaire;
        private String montant;
        private String devise;
        private String dateVirement;

        public Builder compteEmetteur(String compteEmetteur) {
            this.compteEmetteur = compteEmetteur;
            return this;
        }

        public Builder compteBeneficiaire(String compteBeneficiaire) {
            this.compteBeneficiaire = compteBeneficiaire;
            return this;
        }

        public Builder montant(String montant) {
            this.montant = montant;
            return this;
        }

        public Builder devise(String devise) {
            this.devise = devise;
            return this;
        }

        public Builder dateVirement(String dateVirement) {
            this.dateVirement = dateVirement;
            return this;
        }

        public Virement build() {
            Virement virement = new Virement();
            virement.compteEmetteur = this.compteEmetteur;
            virement.compteBeneficiaire = this.compteBeneficiaire;
            virement.devise = this.devise;

            // Traitement et cast du montant
            try {
                if (this.montant != null && !this.montant.trim().isEmpty()) {
                    virement.montant = new BigDecimal(this.montant.trim());
                }
            } catch (NumberFormatException e) {
                virement.erreurs.add("Format de montant invalide");
            }

            // Traitement et cast de la date
            try {
                if (this.dateVirement != null && !this.dateVirement.trim().isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    virement.dateVirement = sdf.parse(this.dateVirement.trim());
                }
            } catch (ParseException e) {
                virement.erreurs.add("Format de date invalide (attendu: yyyy-MM-dd)");
            }

            // Effectuer les contrôles unitaires
            virement.effectuerControlesUnitaires();

            return virement;
        }
    }

    /**
     * Contrôles unitaires du virement
     */
    private void effectuerControlesUnitaires() {
        // Contrôle 1: Montant pas négatif
        if (montant == null) {
            erreurs.add("Le montant est obligatoire");
        } else if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            erreurs.add("Le montant doit être strictement positif");
        }

        // Contrôle 2: Compte émetteur non vide
        if (compteEmetteur == null || compteEmetteur.trim().isEmpty()) {
            erreurs.add("Le compte émetteur est obligatoire");
        }

        // Contrôle 3: Compte bénéficiaire non vide
        if (compteBeneficiaire == null || compteBeneficiaire.trim().isEmpty()) {
            erreurs.add("Le compte bénéficiaire est obligatoire");
        }

        // Contrôle 4: Date inférieure ou égale à aujourd'hui
        if (dateVirement == null) {
            erreurs.add("La date du virement est obligatoire");
        } else {
            Date aujourdHui = new Date();
            if (dateVirement.after(aujourdHui)) {
                erreurs.add("La date du virement ne peut pas être dans le futur");
            }
        }

        // Contrôle 5: Devise non vide
        if (devise == null || devise.trim().isEmpty()) {
            erreurs.add("La devise est obligatoire");
        }
    }

    /**
     * Contrôle complexe: Gestion du plafond journalier
     * 
     * @param montantDejaVireAujourdhui montant déjà viré aujourd'hui par le compte
     * @return true si le plafond est respecté
     */
    public boolean verifierPlafondJournalier(BigDecimal montantDejaVireAujourdhui) {
        if (montant == null)
            return false;

        BigDecimal montantTotal = montantDejaVireAujourdhui.add(montant);
        if (montantTotal.compareTo(PLAFOND_JOURNALIER) > 0) {
            erreurs.add("Plafond journalier dépassé. Maximum: " + PLAFOND_JOURNALIER + " AR");
            return false;
        }
        return true;
    }

    /**
     * Vérifie si le virement est valide (tous les contrôles passent)
     */
    public boolean estValide() {
        return erreurs.isEmpty() && "VALIDE".equals(statut);
    }

    /**
     * Marque le virement comme validé après tous les contrôles
     */
    public void valider() {
        if (erreurs.isEmpty()) {
            this.statut = "VALIDE";
        }
    }

    /**
     * Annule le virement
     */
    public void annuler() {
        this.statut = "ANNULE";
    }

    // Getters
    public String getCompteEmetteur() {
        return compteEmetteur;
    }

    public String getCompteBeneficiaire() {
        return compteBeneficiaire;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public String getDevise() {
        return devise;
    }

    public Date getDateVirement() {
        return dateVirement;
    }

    public String getStatut() {
        return statut;
    }

    public List<String> getErreurs() {
        return new ArrayList<>(erreurs);
    }

    public boolean hasErreurs() {
        return !erreurs.isEmpty();
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Virement{" +
                "compteEmetteur='" + compteEmetteur + '\'' +
                ", compteBeneficiaire='" + compteBeneficiaire + '\'' +
                ", montant=" + montant +
                ", devise='" + devise + '\'' +
                ", dateVirement=" + dateVirement +
                ", statut='" + statut + '\'' +
                ", erreurs=" + erreurs +
                '}';
    }
}