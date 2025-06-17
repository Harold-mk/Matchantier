package com.matchantier.util;

import com.matchantier.model.Article;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class StockAlertManager {
    private static StockAlertManager instance;
    private final Set<Long> alertedArticles;
    private JDialog alertDialog;
    private JTextArea alertArea;
    private Timer alertTimer;

    private StockAlertManager() {
        this.alertedArticles = new HashSet<>();
        initializeAlertDialog();
    }

    public static synchronized StockAlertManager getInstance() {
        if (instance == null) {
            instance = new StockAlertManager();
        }
        return instance;
    }

    private void initializeAlertDialog() {
        alertDialog = new JDialog((Frame) null, "⚠️ Alerte Stock", false);
        alertDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        alertDialog.setAlwaysOnTop(true); // Toujours au premier plan
        
        JPanel panel = new JPanel(new BorderLayout());
        alertArea = new JTextArea(10, 40);
        alertArea.setEditable(false);
        alertArea.setLineWrap(true);
        alertArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(alertArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Fermer");
        JButton acknowledgeButton = new JButton("J'ai compris");
        
        closeButton.addActionListener(e -> alertDialog.dispose());
        acknowledgeButton.addActionListener(e -> alertDialog.setVisible(false));
        
        buttonPanel.add(acknowledgeButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        alertDialog.add(panel);
        alertDialog.pack();
        alertDialog.setLocationRelativeTo(null);
    }

    public void checkStockLevels(List<Article> articles, Map<Long, Integer> stockQuantities) {
        StringBuilder alertMessage = new StringBuilder();
        boolean hasAlerts = false;

        for (Article article : articles) {
            int currentStock = stockQuantities.getOrDefault(article.getId(), 0);
            if (currentStock <= article.getQuantiteMinimale()) {
                hasAlerts = true;
                alertMessage.append(String.format("- %s (Code: %s): Stock actuel = %d, Seuil minimum = %d\n",
                    article.getNom(),
                    article.getCodeReference(),
                    currentStock,
                    article.getQuantiteMinimale()));
                
                // Ajouter l'article à la liste des articles alertés
                alertedArticles.add(article.getId());
            } else {
                // Retirer l'article de la liste des articles alertés
                alertedArticles.remove(article.getId());
            }
        }

        if (hasAlerts) {
            showAlert(alertMessage.toString());
        }
    }

    private void showAlert(String message) {
        alertArea.setText("Articles en stock bas :\n\n" + message);
        
        // Afficher la fenêtre d'alerte
        if (!alertDialog.isVisible()) {
            alertDialog.setVisible(true);
            // Forcer la fenêtre au premier plan
            alertDialog.toFront();
            alertDialog.requestFocus();
        }
        
        // Jouer un son d'alerte
        Toolkit.getDefaultToolkit().beep();
    }

    public boolean isArticleAlerted(Long articleId) {
        return alertedArticles.contains(articleId);
    }

    public void clearAlerts() {
        alertedArticles.clear();
        if (alertDialog != null) {
            alertDialog.dispose();
        }
    }
} 