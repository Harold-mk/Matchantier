package com.matchantier.ui;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.dao.InventaireDAO;
import com.matchantier.dao.MouvementDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Inventaire;
import com.matchantier.model.Mouvement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RapportsPanel extends JPanel {
    private final ArticleDAO articleDAO;
    private final MouvementDAO mouvementDAO;
    private final InventaireDAO inventaireDAO;
    private final com.toedter.calendar.JDateChooser dateDebut;
    private final com.toedter.calendar.JDateChooser dateFin;
    private final JComboBox<String> rapportType;
    private final JTextArea rapportArea;
    private String rapportActuel;

    public RapportsPanel() {
        articleDAO = new ArticleDAO();
        mouvementDAO = new MouvementDAO();
        inventaireDAO = new InventaireDAO();
        
        // Initialisation des composants
        String[] types = {
            "Rapport de Stock Actuel",
            "Rapport d'Historique des Mouvements",
            "Rapport d'Alertes de Stock",
            "Rapport d'Inventaire"
        };
        rapportType = new JComboBox<>(types);
        dateDebut = new com.toedter.calendar.JDateChooser();
        dateFin = new com.toedter.calendar.JDateChooser();
        rapportArea = new JTextArea();
        rapportArea.setEditable(false);
        
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel de configuration
        JPanel configPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Type de rapport
        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(new JLabel("Type de rapport:"), gbc);
        gbc.gridx = 1;
        configPanel.add(rapportType, gbc);

        // Dates
        gbc.gridx = 0;
        gbc.gridy = 1;
        configPanel.add(new JLabel("Date début:"), gbc);
        gbc.gridx = 1;
        configPanel.add(dateDebut, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        configPanel.add(new JLabel("Date fin:"), gbc);
        gbc.gridx = 1;
        configPanel.add(dateFin, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton genererButton = new JButton("Générer Rapport");
        JButton exporterButton = new JButton("Exporter");
        
        genererButton.addActionListener(e -> genererRapport());
        exporterButton.addActionListener(e -> exporterRapport());
        
        buttonPanel.add(genererButton);
        buttonPanel.add(exporterButton);

        // Zone d'affichage du rapport
        JScrollPane scrollPane = new JScrollPane(rapportArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // Ajout des composants
        add(configPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void genererRapport() {
        String type = (String) rapportType.getSelectedItem();
        
        // Vérification des dates pour les rapports qui en ont besoin
        if (type.equals("Rapport d'Historique des Mouvements")) {
            if (dateDebut.getDate() == null || dateFin.getDate() == null) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner les dates de début et de fin",
                        "Dates manquantes",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dateDebut.getDate().after(dateFin.getDate())) {
                JOptionPane.showMessageDialog(this,
                        "La date de début doit être antérieure à la date de fin",
                        "Dates invalides",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            switch (type) {
                case "Rapport de Stock Actuel":
                    genererRapportStock();
                    break;
                case "Rapport d'Historique des Mouvements":
                    genererRapportMouvements();
                    break;
                case "Rapport d'Alertes de Stock":
                    genererRapportAlertes();
                    break;
                case "Rapport d'Inventaire":
                    genererRapportInventaire();
                    break;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération du rapport: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void genererRapportStock() throws SQLException {
        List<Article> articles = articleDAO.findAll();
        StringBuilder rapport = new StringBuilder();
        rapport.append("Rapport de Stock Actuel\n");
        rapport.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");
        rapport.append("Article\t\tStock Actuel\tSeuil d'Alerte\n");
        rapport.append("----------------------------------------\n");

        for (Article article : articles) {
            int stockActuel = mouvementDAO.getStockActuel(article.getId());
            rapport.append(String.format("%s\t\t%d\t\t%d\n",
                    article.getNom(),
                    stockActuel,
                    article.getQuantiteMinimale()));
        }

        rapportActuel = rapport.toString();
        rapportArea.setText(rapportActuel);
    }

    private void genererRapportMouvements() throws SQLException {
        LocalDateTime debut = dateDebut.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime fin = dateFin.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        List<Mouvement> mouvements = mouvementDAO.findByDateRange(debut, fin);

        rapportActuel = generateMouvementsReport(mouvements);
        rapportArea.setText(rapportActuel);
    }

    private String generateMouvementsReport(List<Mouvement> mouvements) {
        StringBuilder report = new StringBuilder();
        report.append("RAPPORT DES MOUVEMENTS DE STOCK\n");
        report.append("Période: ").append(dateDebut.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE))
              .append(" au ").append(dateFin.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE))
              .append("\n");
        report.append("==============================\n\n");

        for (Mouvement m : mouvements) {
            try {
                Article article = articleDAO.findById(m.getArticleId()).orElse(null);
                String articleName = article != null ? article.getNom() : "Article inconnu";
                
                report.append(String.format("ID: %d\n", m.getId()));
                report.append(String.format("Article: %s\n", articleName));
                report.append(String.format("Type: %s\n", m.getType()));
                report.append(String.format("Quantité: %d\n", m.getQuantite()));
                report.append(String.format("Date: %s\n", m.getDateMouvement()));
                report.append(String.format("Référence: %s\n", m.getReference()));
                report.append(String.format("Commentaire: %s\n", m.getCommentaire()));
                report.append("----------------------------\n");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return report.toString();
    }

    private void genererRapportAlertes() throws SQLException {
        List<Article> articles = articleDAO.findByStockBas();
        StringBuilder rapport = new StringBuilder();
        rapport.append("Rapport d'Alertes de Stock\n");
        rapport.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");
        rapport.append("Article\t\tStock Actuel\tSeuil d'Alerte\n");
        rapport.append("----------------------------------------\n");

        for (Article article : articles) {
            int stockActuel = mouvementDAO.getStockActuel(article.getId());
            rapport.append(String.format("%s\t\t%d\t\t%d\n",
                    article.getNom(),
                    stockActuel,
                    article.getQuantiteMinimale()));
        }

        rapportActuel = rapport.toString();
        rapportArea.setText(rapportActuel);
    }

    private void genererRapportInventaire() throws SQLException {
        List<Inventaire> inventaires = inventaireDAO.findAll();
        StringBuilder rapport = new StringBuilder();
        rapport.append("Rapport d'Inventaires\n");
        rapport.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");

        for (Inventaire inventaire : inventaires) {
            rapport.append("Inventaire du ").append(inventaire.getDateInventaire().format(DateTimeFormatter.ISO_LOCAL_DATE))
                   .append(" (").append(inventaire.getStatut().name()).append(")\n");
            rapport.append("Article\t\tThéorique\tRéel\tÉcart\n");
            rapport.append("----------------------------------------\n");

            for (Inventaire.DetailInventaire detail : inventaire.getDetails()) {
                Article article = articleDAO.findById(detail.getArticleId()).orElse(null);
                String articleName = article != null ? article.getNom() : "Article inconnu";
                rapport.append(String.format("%s\t\t%d\t\t%d\t%d\n",
                        articleName,
                        detail.getQuantiteTheorique(),
                        detail.getQuantiteReelle(),
                        detail.getEcart()));
            }
            rapport.append("\n");
        }

        rapportActuel = rapport.toString();
        rapportArea.setText(rapportActuel);
    }

    private void exporterRapport() {
        if (rapportActuel == null || rapportActuel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucun rapport à exporter. Veuillez d'abord générer un rapport.",
                    "Aucun rapport",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le rapport");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers texte", "txt"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String extension = "";
            if (fileChooser.getFileFilter() instanceof javax.swing.filechooser.FileNameExtensionFilter) {
                extension = "." + ((javax.swing.filechooser.FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
            }
            if (!file.getName().toLowerCase().endsWith(extension)) {
                file = new File(file.getAbsolutePath() + extension);
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(rapportActuel);
                JOptionPane.showMessageDialog(this,
                        "Le rapport a été exporté avec succès.",
                        "Exportation réussie",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'exportation du rapport: " + e.getMessage(),
                        "Erreur d'exportation",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 