package com.matchantier.ui;

import com.matchantier.util.DatabaseResetter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private final JTabbedPane tabbedPane;
    private final StocksPanel stocksPanel;
    private final ArticlesPanel articlesPanel;
    private final MouvementsPanel mouvementsPanel;
    private final InventairesPanel inventairesPanel;
    private final RapportsPanel rapportsPanel;
    private final DatabaseResetter databaseResetter;

    public MainWindow() {
        setTitle("Gestion de Stock - Matchantier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialisation des composants
        databaseResetter = new DatabaseResetter();
        stocksPanel = new StocksPanel();
        articlesPanel = new ArticlesPanel();
        mouvementsPanel = new MouvementsPanel();
        inventairesPanel = new InventairesPanel();
        rapportsPanel = new RapportsPanel();

        // Connexion entre MouvementsPanel et StocksPanel
        mouvementsPanel.addStockChangeListener(stocksPanel);

        // Création du menu
        createMenuBar();

        // Création du tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Stocks", new ImageIcon("icons/stock.png"), stocksPanel);
        tabbedPane.addTab("Articles", new ImageIcon("icons/article.png"), articlesPanel);
        tabbedPane.addTab("Mouvements", new ImageIcon("icons/mouvement.png"), mouvementsPanel);
        tabbedPane.addTab("Inventaires", new ImageIcon("icons/inventaire.png"), inventairesPanel);
        tabbedPane.addTab("Rapports", new ImageIcon("icons/rapport.png"), rapportsPanel);

        add(tabbedPane);

        // Ajout d'un WindowListener pour gérer la fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Sauvegarder l'état de l'application si nécessaire
                System.exit(0);
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu Outils
        JMenu toolsMenu = new JMenu("Outils");
        JMenuItem resetDatabaseItem = new JMenuItem("Réinitialiser la base de données");
        resetDatabaseItem.addActionListener(e -> databaseResetter.resetDatabaseWithConfirmation());
        toolsMenu.add(resetDatabaseItem);
        
        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Gestion de Stock - Matchantier\n" +
            "Version 1.0\n\n" +
            "Application de gestion de stock pour Matchantier.",
            "À propos",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 