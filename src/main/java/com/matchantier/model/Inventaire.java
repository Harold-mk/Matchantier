package com.matchantier.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Inventaire {
    private Long id;
    private LocalDateTime dateInventaire;
    private StatutInventaire statut;
    private String raisonAjustement;
    private List<DetailInventaire> details;

    public enum StatutInventaire {
        EN_COURS,
        TERMINE
    }

    public Inventaire() {
        this.details = new ArrayList<>();
    }

    public Inventaire(LocalDateTime dateInventaire) {
        this();
        this.dateInventaire = dateInventaire;
        this.statut = StatutInventaire.EN_COURS;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateInventaire() {
        return dateInventaire;
    }

    public void setDateInventaire(LocalDateTime dateInventaire) {
        this.dateInventaire = dateInventaire;
    }

    public StatutInventaire getStatut() {
        return statut;
    }

    public void setStatut(StatutInventaire statut) {
        this.statut = statut;
    }

    public String getRaisonAjustement() {
        return raisonAjustement;
    }

    public void setRaisonAjustement(String raisonAjustement) {
        this.raisonAjustement = raisonAjustement;
    }

    public List<DetailInventaire> getDetails() {
        return details;
    }

    public void setDetails(List<DetailInventaire> details) {
        this.details = details;
    }

    public void addDetail(DetailInventaire detail) {
        this.details.add(detail);
    }

    public static class DetailInventaire {
        private Long id;
        private Long articleId;
        private int quantiteTheorique;
        private int quantiteReelle;
        private int ecart;

        public DetailInventaire() {}

        public DetailInventaire(Long articleId, int quantiteTheorique, int quantiteReelle) {
            this.articleId = articleId;
            this.quantiteTheorique = quantiteTheorique;
            this.quantiteReelle = quantiteReelle;
            this.ecart = quantiteReelle - quantiteTheorique;
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

        public int getQuantiteTheorique() {
            return quantiteTheorique;
        }

        public void setQuantiteTheorique(int quantiteTheorique) {
            this.quantiteTheorique = quantiteTheorique;
            this.ecart = this.quantiteReelle - quantiteTheorique;
        }

        public int getQuantiteReelle() {
            return quantiteReelle;
        }

        public void setQuantiteReelle(int quantiteReelle) {
            this.quantiteReelle = quantiteReelle;
            this.ecart = quantiteReelle - this.quantiteTheorique;
        }

        public int getEcart() {
            return ecart;
        }
    }
} 