package com.matchantier.ui.dialog;

import com.matchantier.model.Article;

import javax.swing.*;
import java.awt.*;

public class ArticleDialog extends JDialog {
    private JTextField codeField;
    private JTextField nomField;
    private JTextArea descriptionArea;
    private JTextField uniteField;
    private JSpinner quantiteMinSpinner;
    private JSpinner prixSpinner;
    private JComboBox<String> categorieCombo;
    private Article article;
    private boolean confirmed = false;

    public ArticleDialog(Frame parent, Article article) {
        super(parent, "Article", true);
        this.article = article;
        initializeComponents();
        setupLayout();
        if (article != null) {
            fillFields();
        }
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        // Champs de saisie
        codeField = new JTextField(15);
        nomField = new JTextField(15);
        descriptionArea = new JTextArea(3, 15);
        uniteField = new JTextField(5);
        quantiteMinSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        prixSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 0.01));
        
        String[] categories = {"Matériaux", "Outillage", "Équipement", "Consommables"};
        categorieCombo = new JComboBox<>(categories);

        // Configuration des spinners
        JSpinner.NumberEditor prixEditor = new JSpinner.NumberEditor(prixSpinner, "0.00");
        prixSpinner.setEditor(prixEditor);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Code référence
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Code référence *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(codeField, gbc);

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Nom *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nomField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(descScrollPane, gbc);

        // Unité de mesure
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Unité de mesure *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(uniteField, gbc);

        // Quantité minimale
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Quantité minimale *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(quantiteMinSpinner, gbc);

        // Prix unitaire
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Prix unitaire *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prixSpinner, gbc);

        // Catégorie
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Catégorie *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(categorieCombo, gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> saveArticle());
        cancelButton.addActionListener(e -> dispose());

        // Ajout des panels
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Ajout d'une note sur les champs obligatoires
        JLabel noteLabel = new JLabel("* Champs obligatoires");
        noteLabel.setForeground(Color.GRAY);
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.add(noteLabel);
        add(notePanel, BorderLayout.NORTH);
    }

    private void fillFields() {
        codeField.setText(article.getCodeReference());
        nomField.setText(article.getNom());
        descriptionArea.setText(article.getDescription());
        uniteField.setText(article.getUniteMesure());
        quantiteMinSpinner.setValue(article.getQuantiteMinimale());
        prixSpinner.setValue(article.getPrixUnitaire());
        categorieCombo.setSelectedItem(article.getCategorie());
    }

    private void saveArticle() {
        // Validation des champs obligatoires
        if (codeField.getText().trim().isEmpty() ||
            nomField.getText().trim().isEmpty() ||
            uniteField.getText().trim().isEmpty() ||
            categorieCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (article == null) {
            article = new Article();
        }

        article.setCodeReference(codeField.getText().trim());
        article.setNom(nomField.getText().trim());
        article.setDescription(descriptionArea.getText().trim());
        article.setUniteMesure(uniteField.getText().trim());
        article.setQuantiteMinimale((Integer) quantiteMinSpinner.getValue());
        article.setPrixUnitaire((Double) prixSpinner.getValue());
        article.setCategorie((String) categorieCombo.getSelectedItem());

        confirmed = true;
        dispose();
    }

    public Article getArticle() {
        return confirmed ? article : null;
    }
} 