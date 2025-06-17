package com.matchantier.ui;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.dao.MouvementDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Mouvement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

public class StocksPanel extends JPanel {
    private final ArticleDAO articleDAO;
    private final MouvementDAO mouvementDAO;
    private JTable stocksTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categorieFilter;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton filterButton;

    public StocksPanel() {
        articleDAO = new ArticleDAO();
        mouvementDAO = new MouvementDAO();
        initializeComponents();
        setupLayout();
        loadStocks();
    }

    private void initializeComponents() {
        // Table des stocks
        String[] columns = {"Code", "Article", "Catégorie", "Unité", "Quantité en stock", "Quantité minimale", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stocksTable = new JTable(tableModel);
        stocksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Filtres
        categorieFilter = new JComboBox<>();
        categorieFilter.addItem("Toutes les catégories");
        searchField = new JTextField(20);

        // Boutons
        refreshButton = new JButton("Actualiser");
        filterButton = new JButton("Filtrer");

        refreshButton.addActionListener(e -> loadStocks());
        filterButton.addActionListener(e -> filterStocks());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel des filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Rechercher:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Catégorie:"));
        filterPanel.add(categorieFilter);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);

        // Table dans un scroll pane
        JScrollPane scrollPane = new JScrollPane(stocksTable);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStocks() {
        try {
            List<Article> articles = articleDAO.findAll();
            Map<Long, Integer> stockQuantities = calculateStockQuantities();
            updateTable(articles, stockQuantities);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des stocks: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<Long, Integer> calculateStockQuantities() throws SQLException {
        Map<Long, Integer> quantities = new HashMap<>();
        List<Mouvement> mouvements = mouvementDAO.findAll();

        for (Mouvement mouvement : mouvements) {
            Long articleId = mouvement.getArticleId();
            int quantity = quantities.getOrDefault(articleId, 0);

            if (mouvement.getType() == Mouvement.Type.ENTREE) {
                quantity += mouvement.getQuantite();
            } else if (mouvement.getType() == Mouvement.Type.SORTIE) {
                quantity -= mouvement.getQuantite();
            }

            quantities.put(articleId, quantity);
        }

        return quantities;
    }

    private void updateTable(List<Article> articles, Map<Long, Integer> stockQuantities) {
        tableModel.setRowCount(0);
        for (Article article : articles) {
            int quantiteEnStock = stockQuantities.getOrDefault(article.getId(), 0);
            String statut = quantiteEnStock <= article.getQuantiteMinimale() ? "Stock bas" : "OK";
            
            tableModel.addRow(new Object[]{
                article.getCodeReference(),
                article.getNom(),
                article.getCategorie(),
                article.getUniteMesure(),
                quantiteEnStock,
                article.getQuantiteMinimale(),
                statut
            });
        }
    }

    private void filterStocks() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categorieFilter.getSelectedItem();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        stocksTable.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String code = entry.getStringValue(0).toLowerCase();
                String nom = entry.getStringValue(1).toLowerCase();
                String categorie = entry.getStringValue(2);

                boolean matchesSearch = searchText.isEmpty() ||
                        code.contains(searchText) ||
                        nom.contains(searchText);
                boolean matchesCategory = selectedCategory.equals("Toutes les catégories") ||
                        categorie.equals(selectedCategory);

                return matchesSearch && matchesCategory;
            }
        };

        sorter.setRowFilter(filter);
    }
} 