package com.matchantier.ui;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.dao.MouvementDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Mouvement;
import com.matchantier.util.StockAlertManager;
import com.matchantier.util.StockCalculator;

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
import java.util.stream.Collectors;

public class StocksPanel extends JPanel implements MouvementsPanel.StockChangeListener {
    private final ArticleDAO articleDAO;
    private final MouvementDAO mouvementDAO;
    private final StockCalculator stockCalculator;
    private final JTable stocksTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> categoryFilter;
    private final JTextField searchField;
    private final JButton refreshButton;
    private final JButton filterButton;
    private final StockAlertManager alertManager;
    private Timer alertCheckTimer;

    public StocksPanel() {
        this.articleDAO = new ArticleDAO();
        this.mouvementDAO = new MouvementDAO();
        this.stockCalculator = new StockCalculator();
        this.alertManager = StockAlertManager.getInstance();
        
        setLayout(new BorderLayout());
        
        // Création du modèle de table
        String[] columns = {"Code", "Nom", "Catégorie", "Quantité", "Unité", "Seuil Minimum", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        stocksTable = new JTable(tableModel);
        stocksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stocksTable.getTableHeader().setReorderingAllowed(false);
        
        // Ajout d'un TableRowSorter pour le filtrage
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        stocksTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(stocksTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        categoryFilter = new JComboBox<>(new String[]{"Toutes", "Matériaux", "Outillage", "Équipement", "Consommables"});
        
        refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> loadStocks());
        
        filterButton = new JButton("Filtrer");
        filterButton.addActionListener(e -> filterStocks());
        
        filterPanel.add(new JLabel("Rechercher:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Catégorie:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(refreshButton);
        filterPanel.add(filterButton);
        
        add(filterPanel, BorderLayout.NORTH);
        
        // Charger les stocks
        loadStocks();
        
        // Configurer le timer pour vérifier les alertes toutes les 5 minutes
        alertCheckTimer = new Timer(300000, e -> checkStockAlerts());
        alertCheckTimer.start();
    }
    
    @Override
    public void onStockChanged() {
        loadStocks();
    }
    
    private void loadStocks() {
        try {
            List<Article> articles = articleDAO.findAll();
            Map<Long, Integer> stockQuantities = calculateStockQuantities();
            
            tableModel.setRowCount(0);
            for (Article article : articles) {
                int quantity = stockQuantities.getOrDefault(article.getId(), 0);
                String status = getStockStatus(quantity, article.getQuantiteMinimale());
                
                tableModel.addRow(new Object[]{
                    article.getCodeReference(),
                    article.getNom(),
                    article.getCategorie(),
                    quantity,
                    article.getUniteMesure(),
                    article.getQuantiteMinimale(),
                    status
                });
            }
            
            // Vérifier les alertes de stock
            checkStockAlerts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des stocks : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkStockAlerts() {
        try {
            List<Article> articles = articleDAO.findAll();
            Map<Long, Integer> stockQuantities = calculateStockQuantities();
            alertManager.checkStockLevels(articles, stockQuantities);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la vérification des alertes : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Map<Long, Integer> calculateStockQuantities() {
        try {
            return stockCalculator.calculateAllStocks();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du calcul des quantités : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return new HashMap<>();
        }
    }
    
    private String getStockStatus(int quantity, int seuilMinimum) {
        if (quantity <= 0) {
            return "Rupture";
        } else if (quantity <= seuilMinimum) {
            return "Stock Bas";
        } else {
            return "OK";
        }
    }
    
    private void filterStocks() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) stocksTable.getRowSorter();
        
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String code = entry.getStringValue(0).toLowerCase();
                String nom = entry.getStringValue(1).toLowerCase();
                String categorie = entry.getStringValue(2);
                
                boolean matchesSearch = code.contains(searchText) || nom.contains(searchText);
                boolean matchesCategory = selectedCategory.equals("Toutes") || categorie.equals(selectedCategory);
                
                return matchesSearch && matchesCategory;
            }
        };
        
        sorter.setRowFilter(filter);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (alertCheckTimer != null) {
            alertCheckTimer.stop();
        }
    }
} 