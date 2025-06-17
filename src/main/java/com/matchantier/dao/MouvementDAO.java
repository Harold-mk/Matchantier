package com.matchantier.dao;

import com.matchantier.database.DatabaseManager;
import com.matchantier.model.Mouvement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MouvementDAO {
    private final DatabaseManager dbManager;

    public MouvementDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Mouvement create(Mouvement mouvement) throws SQLException {
        String sql = "INSERT INTO mouvements (article_id, type, quantite, date_mouvement, reference, commentaire) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, mouvement.getArticleId());
            stmt.setString(2, mouvement.getType().name());
            stmt.setInt(3, mouvement.getQuantite());
            stmt.setTimestamp(4, Timestamp.valueOf(mouvement.getDateMouvement()));
            stmt.setString(5, mouvement.getReference());
            stmt.setString(6, mouvement.getCommentaire());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du mouvement a échoué, aucune ligne n'a été affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mouvement.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création du mouvement a échoué, aucun ID généré.");
                }
            }
            
            return mouvement;
        }
    }

    public Optional<Mouvement> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM mouvements WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMouvement(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Mouvement> findAll() throws SQLException {
        String sql = "SELECT * FROM mouvements ORDER BY date_mouvement DESC";
        List<Mouvement> mouvements = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
        }
        return mouvements;
    }

    public List<Mouvement> findByArticleId(Long articleId) throws SQLException {
        String sql = "SELECT * FROM mouvements WHERE article_id = ? ORDER BY date_mouvement DESC";
        List<Mouvement> mouvements = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, articleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(mapResultSetToMouvement(rs));
                }
            }
        }
        return mouvements;
    }

    public List<Mouvement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM mouvements WHERE date_mouvement BETWEEN ? AND ? ORDER BY date_mouvement DESC";
        List<Mouvement> mouvements = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                mouvements.add(mapResultSetToMouvement(rs));
            }
        }
        return mouvements;
    }

    public int getStockActuel(Long articleId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN type = 'ENTREE' THEN quantite ELSE -quantite END), 0) " +
                    "FROM mouvements WHERE article_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, articleId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Mouvement mapResultSetToMouvement(ResultSet rs) throws SQLException {
        Mouvement mouvement = new Mouvement();
        mouvement.setId(rs.getLong("id"));
        mouvement.setArticleId(rs.getLong("article_id"));
        mouvement.setType(Mouvement.Type.valueOf(rs.getString("type")));
        mouvement.setQuantite(rs.getInt("quantite"));
        mouvement.setDateMouvement(rs.getTimestamp("date_mouvement").toLocalDateTime());
        mouvement.setReference(rs.getString("reference"));
        mouvement.setCommentaire(rs.getString("commentaire"));
        return mouvement;
    }
} 