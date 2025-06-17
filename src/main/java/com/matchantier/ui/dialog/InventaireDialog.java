package com.matchantier.ui.dialog;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Inventaire;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class InventaireDialog extends JDialog {
    private JDateChooser dateInventaireChooser;
    private JComboBox<Inventaire.StatutInventaire> statutCombo;
    private JTextArea raisonAjustementArea;
    private JTable detailsTable;
    private DefaultTableModel tableModel;
    private boolean confirmed = false;
    private Inventaire inventaire;
    private List<Article> articles;

    public InventaireDialog(Frame owner, Inventaire inventaire) {
        super(owner, "Inventaire", true);
        this.inventaire = inventaire;
        initializeComponents();
        setupLayout();
        loadArticles();
        if (inventaire != null) {
            fillFields();
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        // Date d'inventaire
        dateInventaireChooser = new JDateChooser();
        dateInventaireChooser.setDate(new Date());

        // Statut
        statutCombo = new JComboBox<>(Inventaire.StatutInventaire.values());

        // Raison d'ajustement
        raisonAjustementArea = new JTextArea(3, 20);
        raisonAjustementArea.setLineWrap(true);
        raisonAjustementArea.setWrapStyleWord(true);

        // Table des détails
        String[] columns = {"Article", "Quantité théorique", "Quantité réelle", "Écart"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Seule la quantité réelle est éditable
            }
        };
        detailsTable = new JTable(tableModel);

        // Boutons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");

        okButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date d'inventaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Date d'inventaire:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateInventaireChooser, gbc);

        // Statut
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statutCombo, gbc);

        // Raison d'ajustement
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Raison d'ajustement:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JScrollPane(raisonAjustementArea), gbc);

        // Table des détails
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(detailsTable), gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(new JButton("OK"));
        buttonPanel.add(new JButton("Annuler"));

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadArticles() {
        try {
            ArticleDAO articleDAO = new ArticleDAO();
            articles = articleDAO.findAll();
            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des articles: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        if (articles != null) {
            for (Article article : articles) {
                tableModel.addRow(new Object[]{
                    article.getNom(),
                    0, // Quantité théorique initiale
                    0, // Quantité réelle initiale
                    0  // Écart initial
                });
            }
        }
    }

    private void fillFields() {
        if (inventaire != null) {
            dateInventaireChooser.setDate(Date.from(inventaire.getDateInventaire().atZone(ZoneId.systemDefault()).toInstant()));
            statutCombo.setSelectedItem(inventaire.getStatut());
            raisonAjustementArea.setText(inventaire.getRaisonAjustement());

            // Remplir la table avec les détails de l'inventaire
            tableModel.setRowCount(0);
            for (Inventaire.DetailInventaire detail : inventaire.getDetails()) {
                Article article = articles.stream()
                        .filter(a -> a.getId().equals(detail.getArticleId()))
                        .findFirst()
                        .orElse(null);
                if (article != null) {
                    tableModel.addRow(new Object[]{
                        article.getNom(),
                        detail.getQuantiteTheorique(),
                        detail.getQuantiteReelle(),
                        detail.getEcart()
                    });
                }
            }
        }
    }

    private boolean validateFields() {
        if (dateInventaireChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une date d'inventaire.",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Vérifier que toutes les quantités réelles sont valides
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                int quantiteReelle = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                if (quantiteReelle < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Les quantités réelles ne peuvent pas être négatives.",
                            "Erreur de validation",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez entrer des valeurs numériques valides pour les quantités réelles.",
                        "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public Inventaire getInventaire() {
        if (!confirmed) {
            return null;
        }

        Inventaire result = inventaire != null ? inventaire : new Inventaire();
        result.setDateInventaire(dateInventaireChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        result.setStatut((Inventaire.StatutInventaire) statutCombo.getSelectedItem());
        result.setRaisonAjustement(raisonAjustementArea.getText().trim());

        // Mettre à jour les détails de l'inventaire
        result.getDetails().clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Article article = articles.get(i);
            int quantiteTheorique = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            int quantiteReelle = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            Inventaire.DetailInventaire detail = new Inventaire.DetailInventaire(
                article.getId(),
                quantiteTheorique,
                quantiteReelle
            );
            result.addDetail(detail);
        }

        return result;
    }
} 