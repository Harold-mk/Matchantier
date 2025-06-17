package com.matchantier.ui;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.model.Article;
import com.matchantier.ui.dialog.ArticleDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ArticlesPanel extends JPanel {
    private final ArticleDAO articleDAO;
    private JTable articlesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public ArticlesPanel() {
        articleDAO = new ArticleDAO();
        initializeComponents();
        setupLayout();
        loadArticles();
    }

    private void initializeComponents() {
        // Table des articles
        String[] columns = {"ID", "Code", "Nom", "Catégorie", "Qté min.", "Prix unit.", "Unité"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        articlesTable = new JTable(tableModel);
        articlesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Champ de recherche
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchArticles(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchArticles(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchArticles(); }
        });

        // Filtre de catégorie
        categoryFilter = new JComboBox<>(new String[]{"Toutes", "Matériaux", "Outillage", "Équipement", "Consommables"});
        categoryFilter.addActionListener(e -> filterArticles());

        // Boutons
        addButton = new JButton("Nouvel article");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Actualiser");

        addButton.addActionListener(e -> addArticle());
        editButton.addActionListener(e -> editArticle());
        deleteButton.addActionListener(e -> deleteArticle());
        refreshButton.addActionListener(e -> loadArticles());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel de recherche et filtres
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Rechercher:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Catégorie:"));
        searchPanel.add(categoryFilter);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Panel supérieur
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table dans un scroll pane
        JScrollPane scrollPane = new JScrollPane(articlesTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadArticles() {
        try {
            List<Article> articles = articleDAO.findAll();
            updateTable(articles);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des articles: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Article> articles) {
        tableModel.setRowCount(0);
        for (Article article : articles) {
            tableModel.addRow(new Object[]{
                article.getId(),
                article.getCodeReference(),
                article.getNom(),
                article.getCategorie(),
                article.getQuantiteMinimale(),
                article.getPrixUnitaire(),
                article.getUniteMesure()
            });
        }
    }

    private void searchArticles() {
        String searchText = searchField.getText().toLowerCase();
        try {
            List<Article> articles = articleDAO.findAll();
            List<Article> filteredArticles = articles.stream()
                .filter(article -> 
                    article.getCodeReference().toLowerCase().contains(searchText) ||
                    article.getNom().toLowerCase().contains(searchText))
                .toList();
            updateTable(filteredArticles);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la recherche: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterArticles() {
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        try {
            List<Article> articles = articleDAO.findAll();
            List<Article> filteredArticles = articles.stream()
                .filter(article -> selectedCategory.equals("Toutes") || 
                                 article.getCategorie().equals(selectedCategory))
                .toList();
            updateTable(filteredArticles);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du filtrage: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addArticle() {
        ArticleDialog dialog = new ArticleDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        Article newArticle = dialog.getArticle();
        if (newArticle != null) {
            try {
                articleDAO.create(newArticle);
                loadArticles();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la création de l'article: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editArticle() {
        int selectedRow = articlesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un article à modifier",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            Long articleId = (Long) tableModel.getValueAt(selectedRow, 0);
            Article article = articleDAO.findById(articleId).orElse(null);
            if (article != null) {
                ArticleDialog dialog = new ArticleDialog((Frame) SwingUtilities.getWindowAncestor(this), article);
                dialog.setVisible(true);
                Article updatedArticle = dialog.getArticle();
                if (updatedArticle != null) {
                    articleDAO.update(updatedArticle);
                    loadArticles();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification de l'article: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteArticle() {
        int selectedRow = articlesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un article à supprimer",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cet article ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long articleId = (Long) tableModel.getValueAt(selectedRow, 0);
                if (articleDAO.delete(articleId)) {
                    loadArticles();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de supprimer l'article car il est utilisé dans des mouvements.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression de l'article: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 