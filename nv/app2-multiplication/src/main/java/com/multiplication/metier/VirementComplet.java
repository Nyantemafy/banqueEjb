package com.multiplication.metier;

import java.io.Serializable;

/**
 * Classe étendant Virement pour inclure les informations du bénéficiaire
 */
public class VirementComplet extends Virement implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomBeneficiaire;
    private String prenomBeneficiaire;
    private String emailBeneficiaire;
    private String telephoneBeneficiaire;

    public VirementComplet() {
        super();
    }

    /**
     * Crée un VirementComplet à partir d'un Virement existant
     */
    public static VirementComplet fromVirement(Virement virement) {
        VirementComplet virementComplet = new VirementComplet();
        return virementComplet;
    }

    // Getters et Setters pour les infos bénéficiaire
    public String getNomBeneficiaire() {
        return nomBeneficiaire;
    }

    public void setNomBeneficiaire(String nomBeneficiaire) {
        this.nomBeneficiaire = nomBeneficiaire;
    }

    public String getPrenomBeneficiaire() {
        return prenomBeneficiaire;
    }

    public void setPrenomBeneficiaire(String prenomBeneficiaire) {
        this.prenomBeneficiaire = prenomBeneficiaire;
    }

    public String getEmailBeneficiaire() {
        return emailBeneficiaire;
    }

    public void setEmailBeneficiaire(String emailBeneficiaire) {
        this.emailBeneficiaire = emailBeneficiaire;
    }

    public String getTelephoneBeneficiaire() {
        return telephoneBeneficiaire;
    }

    public void setTelephoneBeneficiaire(String telephoneBeneficiaire) {
        this.telephoneBeneficiaire = telephoneBeneficiaire;
    }

    @Override
    public String toString() {
        return "VirementComplet{" +
                "virement=" + super.toString() +
                ", nomBeneficiaire='" + nomBeneficiaire + '\'' +
                ", prenomBeneficiaire='" + prenomBeneficiaire + '\'' +
                ", emailBeneficiaire='" + emailBeneficiaire + '\'' +
                ", telephoneBeneficiaire='" + telephoneBeneficiaire + '\'' +
                '}';
    }
}