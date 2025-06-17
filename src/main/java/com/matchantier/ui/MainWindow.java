package com.matchantier.ui;

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

    public MainWindow() {
        setTitle("Gestion de Stock - Matchantier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialisation des panels
        stocksPanel = new StocksPanel();
        articlesPanel = new ArticlesPanel();
        mouvementsPanel = new MouvementsPanel();
        inventairesPanel = new InventairesPanel();
        rapportsPanel = new RapportsPanel();

        // Connexion entre MouvementsPanel et StocksPanel
        mouvementsPanel.addStockChangeListener(stocksPanel);

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
} 