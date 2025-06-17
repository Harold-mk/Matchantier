package com.matchantier.model;

public class Article {
    private Long id;
    private String nom;
    private String description;
    private String uniteMesure;
    private int quantiteMinimale;
    private double prixUnitaire;
    private String categorie;
    private String codeReference;
    private boolean actif;

    public Article() {}

    public Article(String nom, String description, String uniteMesure, int quantiteMinimale,
                  double prixUnitaire, String categorie, String codeReference) {
        this.nom = nom;
        this.description = description;
        this.uniteMesure = uniteMesure;
        this.quantiteMinimale = quantiteMinimale;
        this.prixUnitaire = prixUnitaire;
        this.categorie = categorie;
        this.codeReference = codeReference;
        this.actif = true;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public int getQuantiteMinimale() {
        return quantiteMinimale;
    }

    public void setQuantiteMinimale(int quantiteMinimale) {
        this.quantiteMinimale = quantiteMinimale;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getCodeReference() {
        return codeReference;
    }

    public void setCodeReference(String codeReference) {
        this.codeReference = codeReference;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return nom + " (" + codeReference + ")";
    }
} 