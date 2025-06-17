package com.matchantier.ui;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.dao.InventaireDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Inventaire;
import com.matchantier.ui.dialog.InventaireDialog;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class InventairesPanel extends JPanel {
    private final InventaireDAO inventaireDAO;
    private final ArticleDAO articleDAO;
    private JTable inventairesTable;
    private DefaultTableModel tableModel;
    private JComboBox<Inventaire.StatutInventaire> statutFilter;
    private JDateChooser dateDebutChooser;
    private JDateChooser dateFinChooser;
    private JButton addButton;
    private JButton refreshButton;
    private JButton filterButton;

    public InventairesPanel() {
        inventaireDAO = new InventaireDAO();
        articleDAO = new ArticleDAO();
        initializeComponents();
        setupLayout();
        loadInventaires();
    }

    private void initializeComponents() {
        // Table des inventaires
        String[] columns = {"ID", "Date", "Statut", "Raison d'ajustement", "Nombre d'articles"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventairesTable = new JTable(tableModel);
        inventairesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Filtres
        statutFilter = new JComboBox<>(Inventaire.StatutInventaire.values());
        statutFilter.insertItemAt(null, 0);
        statutFilter.setSelectedIndex(0);

        dateDebutChooser = new JDateChooser();
        dateFinChooser = new JDateChooser();

        // Boutons
        addButton = new JButton("Nouvel inventaire");
        refreshButton = new JButton("Actualiser");
        filterButton = new JButton("Filtrer");

        addButton.addActionListener(e -> addInventaire());
        refreshButton.addActionListener(e -> loadInventaires());
        filterButton.addActionListener(e -> filterInventaires());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel des filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Statut:"));
        filterPanel.add(statutFilter);
        filterPanel.add(new JLabel("Date début:"));
        filterPanel.add(dateDebutChooser);
        filterPanel.add(new JLabel("Date fin:"));
        filterPanel.add(dateFinChooser);
        filterPanel.add(filterButton);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        // Panel supérieur
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table dans un scroll pane
        JScrollPane scrollPane = new JScrollPane(inventairesTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadInventaires() {
        try {
            List<Inventaire> inventaires = inventaireDAO.findAll();
            updateTable(inventaires);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des inventaires: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Inventaire> inventaires) {
        tableModel.setRowCount(0);
        for (Inventaire inventaire : inventaires) {
            tableModel.addRow(new Object[]{
                inventaire.getId(),
                inventaire.getDateInventaire(),
                inventaire.getStatut(),
                inventaire.getRaisonAjustement(),
                inventaire.getDetails().size()
            });
        }
    }

    private void filterInventaires() {
        try {
            Inventaire.StatutInventaire selectedStatut = (Inventaire.StatutInventaire) statutFilter.getSelectedItem();
            Date dateDebut = dateDebutChooser.getDate();
            Date dateFin = dateFinChooser.getDate();

            List<Inventaire> inventaires = inventaireDAO.findAll();
            List<Inventaire> filteredInventaires = inventaires.stream()
                .filter(inventaire -> {
                    boolean matchesStatut = selectedStatut == null || 
                                         inventaire.getStatut() == selectedStatut;
                    boolean matchesDate = true;
                    if (dateDebut != null) {
                        LocalDateTime dateDebutLDT = dateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        matchesDate = matchesDate && !inventaire.getDateInventaire().isBefore(dateDebutLDT);
                    }
                    if (dateFin != null) {
                        LocalDateTime dateFinLDT = dateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        matchesDate = matchesDate && !inventaire.getDateInventaire().isAfter(dateFinLDT);
                    }
                    return matchesStatut && matchesDate;
                })
                .toList();
            updateTable(filteredInventaires);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du filtrage des inventaires: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addInventaire() {
        InventaireDialog dialog = new InventaireDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        Inventaire newInventaire = dialog.getInventaire();
        if (newInventaire != null) {
            try {
                inventaireDAO.create(newInventaire);
                loadInventaires();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la création de l'inventaire: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 