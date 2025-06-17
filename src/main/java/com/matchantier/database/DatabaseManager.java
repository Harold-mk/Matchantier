package com.matchantier.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:matchantier.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // Charger le driver SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Créer la connexion
            connection = DriverManager.getConnection(DB_URL);
            
            // Créer les tables si elles n'existent pas
            createTables();
            
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Table Articles
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS articles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    description TEXT,
                    unite_mesure TEXT NOT NULL,
                    quantite_minimale INTEGER NOT NULL,
                    prix_unitaire REAL NOT NULL,
                    categorie TEXT NOT NULL,
                    code_reference TEXT UNIQUE NOT NULL,
                    actif BOOLEAN DEFAULT 1
                )
            """);

            // Table Mouvements
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mouvements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    article_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    quantite INTEGER NOT NULL,
                    date_mouvement TIMESTAMP NOT NULL,
                    reference TEXT,
                    commentaire TEXT,
                    FOREIGN KEY (article_id) REFERENCES articles(id)
                )
            """);

            // Table Inventaires
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS inventaires (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date_inventaire TIMESTAMP NOT NULL,
                    statut TEXT NOT NULL,
                    raison_ajustement TEXT
                )
            """);

            // Table DetailsInventaire
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS details_inventaire (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    inventaire_id INTEGER NOT NULL,
                    article_id INTEGER NOT NULL,
                    quantite_theorique INTEGER NOT NULL,
                    quantite_reelle INTEGER NOT NULL,
                    ecart INTEGER NOT NULL,
                    FOREIGN KEY (inventaire_id) REFERENCES inventaires(id),
                    FOREIGN KEY (article_id) REFERENCES articles(id)
                )
            """);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la fermeture de la connexion", e);
        }
    }
} 