package com.matchantier.dao;

import com.matchantier.database.DatabaseManager;
import com.matchantier.model.Inventaire;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventaireDAO {
    private final DatabaseManager dbManager;

    public InventaireDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Inventaire create(Inventaire inventaire) throws SQLException {
        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);

            // Insérer l'inventaire
            String sql = "INSERT INTO inventaires (date_inventaire, statut, raison_ajustement) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(inventaire.getDateInventaire()));
                pstmt.setString(2, inventaire.getStatut().name());
                pstmt.setString(3, inventaire.getRaisonAjustement());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("La création de l'inventaire a échoué, aucune ligne n'a été affectée.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        inventaire.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("La création de l'inventaire a échoué, aucun ID généré.");
                    }
                }
            }

            // Insérer les détails de l'inventaire
            String detailSql = "INSERT INTO details_inventaire (inventaire_id, article_id, quantite_theorique, " +
                             "quantite_reelle, ecart) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(detailSql)) {
                for (Inventaire.DetailInventaire detail : inventaire.getDetails()) {
                    pstmt.setLong(1, inventaire.getId());
                    pstmt.setLong(2, detail.getArticleId());
                    pstmt.setInt(3, detail.getQuantiteTheorique());
                    pstmt.setInt(4, detail.getQuantiteReelle());
                    pstmt.setInt(5, detail.getEcart());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            return inventaire;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Optional<Inventaire> findById(Long id) throws SQLException {
        String sql = "SELECT i.*, d.* FROM inventaires i " +
                    "LEFT JOIN details_inventaire d ON i.id = d.inventaire_id " +
                    "WHERE i.id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Inventaire inventaire = mapResultSetToInventaire(rs);
                do {
                    if (rs.getLong("article_id") != 0) {
                        inventaire.addDetail(mapResultSetToDetailInventaire(rs));
                    }
                } while (rs.next());
                return Optional.of(inventaire);
            }
        }
        return Optional.empty();
    }

    public List<Inventaire> findAll() throws SQLException {
        String sql = "SELECT i.*, d.* FROM inventaires i " +
                    "LEFT JOIN details_inventaire d ON i.id = d.inventaire_id " +
                    "ORDER BY i.date_inventaire DESC";
        List<Inventaire> inventaires = new ArrayList<>();
        Inventaire currentInventaire = null;

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long inventaireId = rs.getLong("id");
                if (currentInventaire == null || !currentInventaire.getId().equals(inventaireId)) {
                    if (currentInventaire != null) {
                        inventaires.add(currentInventaire);
                    }
                    currentInventaire = mapResultSetToInventaire(rs);
                }
                if (rs.getLong("article_id") != 0) {
                    currentInventaire.addDetail(mapResultSetToDetailInventaire(rs));
                }
            }
            if (currentInventaire != null) {
                inventaires.add(currentInventaire);
            }
        }
        return inventaires;
    }

    public boolean updateStatut(Long id, Inventaire.StatutInventaire statut) throws SQLException {
        String sql = "UPDATE inventaires SET statut = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statut.name());
            pstmt.setLong(2, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    private Inventaire mapResultSetToInventaire(ResultSet rs) throws SQLException {
        Inventaire inventaire = new Inventaire();
        inventaire.setId(rs.getLong("id"));
        inventaire.setDateInventaire(rs.getTimestamp("date_inventaire").toLocalDateTime());
        inventaire.setStatut(Inventaire.StatutInventaire.valueOf(rs.getString("statut")));
        inventaire.setRaisonAjustement(rs.getString("raison_ajustement"));
        return inventaire;
    }

    private Inventaire.DetailInventaire mapResultSetToDetailInventaire(ResultSet rs) throws SQLException {
        return new Inventaire.DetailInventaire(
            rs.getLong("article_id"),
            rs.getInt("quantite_theorique"),
            rs.getInt("quantite_reelle")
        );
    }
} 