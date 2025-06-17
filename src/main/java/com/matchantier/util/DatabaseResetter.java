package com.matchantier.util;

import com.matchantier.database.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class DatabaseResetter {
    private final DatabaseManager dbManager;

    public DatabaseResetter() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public void resetDatabase() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Désactiver les contraintes de clé étrangère pour SQLite
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Liste des tables à vider dans l'ordre correct (en tenant compte des dépendances)
            List<String> tables = Arrays.asList(
                "details_inventaire",  // D'abord les tables avec des clés étrangères
                "mouvements",
                "inventaires",
                "articles"            // Ensuite les tables principales
            );

            // Vider chaque table
            for (String table : tables) {
                stmt.execute("DELETE FROM " + table);
                // Réinitialiser l'auto-incrémentation pour SQLite
                stmt.execute("DELETE FROM sqlite_sequence WHERE name='" + table + "'");
            }

            // Réactiver les contraintes de clé étrangère pour SQLite
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    public void resetDatabaseWithConfirmation() {
        int response = JOptionPane.showConfirmDialog(
            null,
            "Êtes-vous sûr de vouloir réinitialiser la base de données ?\n" +
            "Cette action supprimera toutes les données et ne peut pas être annulée.",
            "Confirmation de réinitialisation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            try {
                resetDatabase();
                JOptionPane.showMessageDialog(
                    null,
                    "La base de données a été réinitialisée avec succès.",
                    "Réinitialisation réussie",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Erreur lors de la réinitialisation de la base de données : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
} 