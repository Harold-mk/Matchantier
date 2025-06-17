package com.matchantier.ui.dialog;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Mouvement;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MouvementDialog extends JDialog {
    private final ArticleDAO articleDAO;
    private JComboBox<Article> articleCombo;
    private JComboBox<Mouvement.Type> typeCombo;
    private JSpinner quantiteSpinner;
    private JTextField referenceField;
    private JTextArea commentaireArea;
    private Mouvement mouvement;
    private boolean confirmed = false;

    public MouvementDialog(Frame parent, Mouvement mouvement) {
        super(parent, "Mouvement", true);
        this.mouvement = mouvement;
        this.articleDAO = new ArticleDAO();
        initializeComponents();
        setupLayout();
        if (mouvement != null) {
            fillFields();
        }
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        // Liste des articles
        try {
            List<Article> articles = articleDAO.findAll();
            articleCombo = new JComboBox<>(articles.toArray(new Article[0]));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des articles: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            articleCombo = new JComboBox<>();
        }

        // Type de mouvement
        typeCombo = new JComboBox<>(Mouvement.Type.values());

        // Quantité
        quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));

        // Référence
        referenceField = new JTextField(15);

        // Commentaire
        commentaireArea = new JTextArea(3, 15);
        commentaireArea.setLineWrap(true);
        commentaireArea.setWrapStyleWord(true);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Article
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Article *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(articleCombo, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Type *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(typeCombo, gbc);

        // Quantité
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Quantité *:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(quantiteSpinner, gbc);

        // Référence
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Référence:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(referenceField, gbc);

        // Commentaire
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Commentaire:"), gbc);
        gbc.gridx = 1;
        JScrollPane commentaireScrollPane = new JScrollPane(commentaireArea);
        mainPanel.add(commentaireScrollPane, gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> saveMouvement());
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
        if (mouvement != null) {
            Article article = null;
            try {
                article = articleDAO.findById(mouvement.getArticleId()).orElse(null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (article != null) {
                articleCombo.setSelectedItem(article);
            }
            typeCombo.setSelectedItem(mouvement.getType());
            quantiteSpinner.setValue(mouvement.getQuantite());
            referenceField.setText(mouvement.getReference());
            commentaireArea.setText(mouvement.getCommentaire());
        }
    }

    private void saveMouvement() {
        // Validation des champs obligatoires
        if (articleCombo.getSelectedItem() == null || typeCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mouvement == null) {
            mouvement = new Mouvement();
        }

        Article selectedArticle = (Article) articleCombo.getSelectedItem();
        mouvement.setArticleId(selectedArticle.getId());
        mouvement.setType((Mouvement.Type) typeCombo.getSelectedItem());
        mouvement.setQuantite((Integer) quantiteSpinner.getValue());
        mouvement.setReference(referenceField.getText().trim());
        mouvement.setCommentaire(commentaireArea.getText().trim());

        confirmed = true;
        dispose();
    }

    public Mouvement getMouvement() {
        return confirmed ? mouvement : null;
    }
} 