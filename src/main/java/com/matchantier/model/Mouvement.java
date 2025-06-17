package com.matchantier.model;

import java.time.LocalDateTime;

public class Mouvement {
    private Long id;
    private Long articleId;
    private Type type;
    private int quantite;
    private LocalDateTime dateMouvement;
    private String reference;
    private String commentaire;

    public enum Type {
        ENTREE,
        SORTIE
    }

    public Mouvement() {
        this.dateMouvement = LocalDateTime.now();
    }

    public Mouvement(Long articleId, Type type, int quantite, String reference, String commentaire) {
        this();
        this.articleId = articleId;
        this.type = type;
        this.quantite = quantite;
        this.reference = reference;
        this.commentaire = commentaire;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
} 