package com.matchantier.ui;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.dao.MouvementDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Mouvement;
import com.matchantier.ui.dialog.MouvementDialog;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class MouvementsPanel extends JPanel {
    private final MouvementDAO mouvementDAO;
    private final ArticleDAO articleDAO;
    private JTable mouvementsTable;
    private DefaultTableModel tableModel;
    private JComboBox<Article> articleFilter;
    private JComboBox<Mouvement.Type> typeFilter;
    private JDateChooser dateDebutChooser;
    private JDateChooser dateFinChooser;
    private JButton addButton;
    private JButton refreshButton;
    private JButton filterButton;
    private final List<StockChangeListener> stockChangeListeners;

    public MouvementsPanel() {
        mouvementDAO = new MouvementDAO();
        articleDAO = new ArticleDAO();
        stockChangeListeners = new ArrayList<>();
        initializeComponents();
        setupLayout();
        loadMouvements();
    }

    private void initializeComponents() {
        // Table des mouvements
        String[] columns = {"ID", "Article", "Type", "Quantité", "Date", "Référence", "Commentaire"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mouvementsTable = new JTable(tableModel);
        mouvementsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Filtres
        try {
            articleFilter = new JComboBox<>(articleDAO.findAll().toArray(new Article[0]));
            articleFilter.insertItemAt(null, 0);
            articleFilter.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des articles: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            articleFilter = new JComboBox<>();
        }

        typeFilter = new JComboBox<>(Mouvement.Type.values());
        typeFilter.insertItemAt(null, 0);
        typeFilter.setSelectedIndex(0);

        dateDebutChooser = new JDateChooser();
        dateFinChooser = new JDateChooser();

        // Boutons
        addButton = new JButton("Nouveau mouvement");
        refreshButton = new JButton("Actualiser");
        filterButton = new JButton("Filtrer");

        addButton.addActionListener(e -> addMouvement());
        refreshButton.addActionListener(e -> loadMouvements());
        filterButton.addActionListener(e -> filterMouvements());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel des filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Article:"));
        filterPanel.add(articleFilter);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
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
        JScrollPane scrollPane = new JScrollPane(mouvementsTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMouvements() {
        try {
            List<Mouvement> mouvements = mouvementDAO.findAll();
            updateTable(mouvements);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des mouvements: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Mouvement> mouvements) {
        DefaultTableModel model = (DefaultTableModel) mouvementsTable.getModel();
        model.setRowCount(0);

        for (Mouvement m : mouvements) {
            try {
                Article article = articleDAO.findById(m.getArticleId()).orElse(null);
                String articleName = article != null ? article.getNom() : "Article inconnu";
                model.addRow(new Object[]{
                    m.getId(),
                    articleName,
                    m.getType(),
                    m.getQuantite(),
                    m.getDateMouvement(),
                    m.getReference(),
                    m.getCommentaire()
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void filterMouvements() {
        try {
            Article selectedArticle = (Article) articleFilter.getSelectedItem();
            Mouvement.Type selectedType = (Mouvement.Type) typeFilter.getSelectedItem();
            Date dateDebut = dateDebutChooser.getDate();
            Date dateFin = dateFinChooser.getDate();

            List<Mouvement> mouvements = mouvementDAO.findAll();
            List<Mouvement> filteredMouvements = mouvements.stream()
                .filter(mouvement -> {
                    boolean matchesArticle = selectedArticle == null || 
                                          mouvement.getArticleId().equals(selectedArticle.getId());
                    boolean matchesType = selectedType == null || 
                                       mouvement.getType() == selectedType;
                    boolean matchesDate = true;
                    if (dateDebut != null) {
                        LocalDateTime dateDebutLDT = dateDebut.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        matchesDate = matchesDate && !mouvement.getDateMouvement().isBefore(dateDebutLDT);
                    }
                    if (dateFin != null) {
                        LocalDateTime dateFinLDT = dateFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        matchesDate = matchesDate && !mouvement.getDateMouvement().isAfter(dateFinLDT);
                    }
                    return matchesArticle && matchesType && matchesDate;
                })
                .toList();
            updateTable(filteredMouvements);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du filtrage des mouvements: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMouvement() {
        MouvementDialog dialog = new MouvementDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        Mouvement newMouvement = dialog.getMouvement();
        if (newMouvement != null) {
            try {
                mouvementDAO.create(newMouvement);
                loadMouvements();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la création du mouvement: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void addStockChangeListener(StockChangeListener listener) {
        stockChangeListeners.add(listener);
    }

    private void notifyStockChange() {
        for (StockChangeListener listener : stockChangeListeners) {
            listener.onStockChanged();
        }
    }

    public interface StockChangeListener {
        void onStockChanged();
    }
} 