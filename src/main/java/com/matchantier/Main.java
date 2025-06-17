package com.matchantier;

import com.matchantier.database.DatabaseManager;
import com.matchantier.ui.MainWindow;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialiser la base de données
            DatabaseManager.getInstance();
            
            // Définir le look and feel du système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Lancer l'application dans l'EDT
            SwingUtilities.invokeLater(() -> {
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors du démarrage de l'application: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
} 