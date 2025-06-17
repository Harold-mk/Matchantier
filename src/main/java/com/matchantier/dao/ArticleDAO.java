package com.matchantier.dao;

import com.matchantier.database.DatabaseManager;
import com.matchantier.model.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArticleDAO {
    private final DatabaseManager dbManager;

    public ArticleDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Article create(Article article) throws SQLException {
        String sql = "INSERT INTO articles (nom, description, unite_mesure, quantite_minimale, " +
                    "prix_unitaire, categorie, code_reference) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, article.getNom());
            stmt.setString(2, article.getDescription());
            stmt.setString(3, article.getUniteMesure());
            stmt.setInt(4, article.getQuantiteMinimale());
            stmt.setDouble(5, article.getPrixUnitaire());
            stmt.setString(6, article.getCategorie());
            stmt.setString(7, article.getCodeReference());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création de l'article a échoué, aucune ligne n'a été affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création de l'article a échoué, aucun ID généré.");
                }
            }
            
            return article;
        }
    }

    public Optional<Article> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM articles WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToArticle(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Article> findAll() throws SQLException {
        String sql = "SELECT * FROM articles WHERE actif = 1 ORDER BY nom";
        List<Article> articles = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
        }
        return articles;
    }

    public List<Article> findByStockBas() throws SQLException {
        String sql = "SELECT a.*, " +
                    "(SELECT COALESCE(SUM(CASE WHEN m.type = 'ENTREE' THEN m.quantite ELSE -m.quantite END), 0) " +
                    "FROM mouvements m WHERE m.article_id = a.id) as stock_actuel " +
                    "FROM articles a " +
                    "WHERE a.actif = true " +
                    "HAVING stock_actuel <= a.quantite_minimale";

        List<Article> articles = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
        }
        return articles;
    }

    public Article update(Article article) throws SQLException {
        String sql = "UPDATE articles SET nom = ?, description = ?, unite_mesure = ?, " +
                    "quantite_minimale = ?, prix_unitaire = ?, categorie = ?, code_reference = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, article.getNom());
            stmt.setString(2, article.getDescription());
            stmt.setString(3, article.getUniteMesure());
            stmt.setInt(4, article.getQuantiteMinimale());
            stmt.setDouble(5, article.getPrixUnitaire());
            stmt.setString(6, article.getCategorie());
            stmt.setString(7, article.getCodeReference());
            stmt.setLong(8, article.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La mise à jour de l'article a échoué, aucune ligne n'a été affectée.");
            }
            
            return article;
        }
    }

    public boolean delete(Long id) throws SQLException {
        // Vérifier si l'article est utilisé dans des mouvements
        String checkSql = "SELECT COUNT(*) FROM mouvements WHERE article_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setLong(1, id);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // L'article est utilisé dans des mouvements
                }
            }
        }

        // Si l'article n'est pas utilisé, le marquer comme inactif
        String sql = "UPDATE articles SET actif = 0 WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getLong("id"));
        article.setNom(rs.getString("nom"));
        article.setDescription(rs.getString("description"));
        article.setUniteMesure(rs.getString("unite_mesure"));
        article.setQuantiteMinimale(rs.getInt("quantite_minimale"));
        article.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        article.setCategorie(rs.getString("categorie"));
        article.setCodeReference(rs.getString("code_reference"));
        article.setActif(rs.getBoolean("actif"));
        return article;
    }
} 