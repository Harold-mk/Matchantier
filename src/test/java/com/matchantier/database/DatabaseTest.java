package com.matchantier.database;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @Test
    public void testDatabaseStructure() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        try (Connection conn = dbManager.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Vérifier que la base de données existe
            assertNotNull(conn, "La connexion à la base de données ne devrait pas être null");
            
            // Vérifier les tables
            Set<String> expectedTables = Set.of("articles", "mouvements", "inventaires", "details_inventaire");
            Set<String> actualTables = new HashSet<>();
            
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                actualTables.add(tables.getString("TABLE_NAME"));
            }
            
            assertTrue(actualTables.containsAll(expectedTables), 
                "Toutes les tables requises devraient exister. Tables trouvées: " + actualTables);
            
            // Vérifier la structure de la table mouvements
            ResultSet columns = metaData.getColumns(null, null, "mouvements", null);
            Set<String> expectedColumns = Set.of(
                "id", "article_id", "type", "quantite", 
                "date_mouvement", "reference", "commentaire"
            );
            Set<String> actualColumns = new HashSet<>();
            
            while (columns.next()) {
                actualColumns.add(columns.getString("COLUMN_NAME"));
            }
            
            assertTrue(actualColumns.containsAll(expectedColumns),
                "La table mouvements devrait contenir toutes les colonnes requises. Colonnes trouvées: " + actualColumns);
            
            // Vérifier la structure de la table details_inventaire
            columns = metaData.getColumns(null, null, "details_inventaire", null);
            expectedColumns = Set.of(
                "id", "inventaire_id", "article_id", 
                "quantite_theorique", "quantite_reelle", "ecart"
            );
            actualColumns.clear();
            
            while (columns.next()) {
                actualColumns.add(columns.getString("COLUMN_NAME"));
            }
            
            assertTrue(actualColumns.containsAll(expectedColumns),
                "La table details_inventaire devrait contenir toutes les colonnes requises. Colonnes trouvées: " + actualColumns);
            
            // Vérifier les clés étrangères de details_inventaire
            ResultSet foreignKeys = metaData.getImportedKeys(null, null, "details_inventaire");
            Set<String> expectedForeignKeys = Set.of("inventaire_id", "article_id");
            Set<String> actualForeignKeys = new HashSet<>();
            
            while (foreignKeys.next()) {
                actualForeignKeys.add(foreignKeys.getString("FKCOLUMN_NAME"));
            }
            
            assertTrue(actualForeignKeys.containsAll(expectedForeignKeys),
                "La table details_inventaire devrait avoir les clés étrangères requises. Clés trouvées: " + actualForeignKeys);
            
        } catch (SQLException e) {
            fail("Erreur lors de la vérification de la base de données: " + e.getMessage());
        }
    }
} 