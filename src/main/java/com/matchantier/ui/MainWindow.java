package com.matchantier.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private ArticlesPanel articlesPanel;
    private MouvementsPanel mouvementsPanel;
    private InventairesPanel inventairesPanel;
    private RapportsPanel rapportsPanel;

    public MainWindow() {
        setTitle("MatChantier - Gestion de Stock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        articlesPanel = new ArticlesPanel();
        mouvementsPanel = new MouvementsPanel();
        inventairesPanel = new InventairesPanel();
        rapportsPanel = new RapportsPanel();

        tabbedPane.addTab("Articles", new ImageIcon("icons/articles.png"), articlesPanel);
        tabbedPane.addTab("Mouvements", new ImageIcon("icons/mouvements.png"), mouvementsPanel);
        tabbedPane.addTab("Inventaires", new ImageIcon("icons/inventaires.png"), inventairesPanel);
        tabbedPane.addTab("Rapports", new ImageIcon("icons/rapports.png"), rapportsPanel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        // Barre d'outils
        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);

        // Barre de statut
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnNouveau = new JButton("Nouveau");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnActualiser = new JButton("Actualiser");

        toolBar.add(btnNouveau);
        toolBar.add(btnModifier);
        toolBar.add(btnSupprimer);
        toolBar.addSeparator();
        toolBar.add(btnActualiser);

        return toolBar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel("Prêt");
        statusBar.add(statusLabel, BorderLayout.WEST);

        return statusBar;
    }
} 