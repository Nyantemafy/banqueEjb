package com.multiplication.model;

import java.io.Serializable;

public class Devise implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomDevise;
    private String dateDebut;
    private String dateFin;
    private double cours;

    public Devise() {
    }

    public Devise(String nomDevise, String dateDebut, String dateFin, double cours) {
        this.nomDevise = nomDevise;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.cours = cours;
    }

    public String getNomDevise() {
        return nomDevise;
    }

    public void setNomDevise(String nomDevise) {
        this.nomDevise = nomDevise;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public double getCours() {
        return cours;
    }

    public void setCours(double cours) {
        this.cours = cours;
    }
}