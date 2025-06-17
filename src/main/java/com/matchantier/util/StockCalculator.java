package com.matchantier.util;

import com.matchantier.dao.ArticleDAO;
import com.matchantier.dao.MouvementDAO;
import com.matchantier.model.Article;
import com.matchantier.model.Mouvement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class StockCalculator {
    private final ArticleDAO articleDAO;
    private final MouvementDAO mouvementDAO;

    public StockCalculator() {
        this.articleDAO = new ArticleDAO();
        this.mouvementDAO = new MouvementDAO();
    }

    /**
     * Calcule la quantité en stock pour un article spécifique
     * @param articleId L'ID de l'article
     * @return La quantité en stock (entrées - sorties)
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    public int calculateStockForArticle(Long articleId) throws SQLException {
        // Récupérer tous les mouvements de l'article
        List<Mouvement> mouvements = mouvementDAO.findByArticleId(articleId);
        
        // Séparer les mouvements en entrées et sorties
        List<Mouvement> mouvementsEntrees = mouvements.stream()
            .filter(m -> m.getType() == Mouvement.Type.ENTREE)
            .collect(Collectors.toList());
            
        List<Mouvement> mouvementsSorties = mouvements.stream()
            .filter(m -> m.getType() == Mouvement.Type.SORTIE)
            .collect(Collectors.toList());
        
        // Calculer la somme des quantités en entrée
        int totalEntrees = mouvementsEntrees.stream()
            .mapToInt(Mouvement::getQuantite)
            .sum();
            
        // Calculer la somme des quantités en sortie
        int totalSorties = mouvementsSorties.stream()
            .mapToInt(Mouvement::getQuantite)
            .sum();
            
        // Retourner la différence (stock actuel)
        return totalEntrees - totalSorties;
    }

    /**
     * Calcule les quantités en stock pour tous les articles
     * @return Une Map avec l'ID de l'article comme clé et la quantité en stock comme valeur
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    public Map<Long, Integer> calculateAllStocks() throws SQLException {
        List<Article> articles = articleDAO.findAll();
        Map<Long, Integer> stockQuantities = new HashMap<>();
        
        for (Article article : articles) {
            int stock = calculateStockForArticle(article.getId());
            stockQuantities.put(article.getId(), stock);
        }
        
        return stockQuantities;
    }

    /**
     * Met à jour la quantité en stock d'un article après un mouvement
     * @param articleId L'ID de l'article
     * @param type Le type de mouvement (ENTREE ou SORTIE)
     * @param quantite La quantité du mouvement
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    public boolean updateStockAfterMovement(Long articleId, String type, int quantite) throws SQLException {
        int stockActuel = calculateStockForArticle(articleId);
        
        // Vérifier si le stock est suffisant pour une sortie
        if (type.equals("SORTIE") && stockActuel < quantite) {
            return false;
        }
        
        return true;
    }

    /**
     * Vérifie si un mouvement de sortie est possible en fonction du stock disponible
     * @param articleId L'ID de l'article
     * @param quantiteSortie La quantité à sortir
     * @return true si le mouvement est possible, false sinon
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    public boolean verifierMouvementSortie(Long articleId, int quantiteSortie) throws SQLException {
        if (quantiteSortie <= 0) {
            throw new IllegalArgumentException("La quantité de sortie doit être supérieure à 0");
        }

        int stockActuel = calculateStockForArticle(articleId);
        return stockActuel >= quantiteSortie;
    }

    /**
     * Vérifie si un mouvement de sortie est possible et retourne un message d'erreur si nécessaire
     * @param articleId L'ID de l'article
     * @param quantiteSortie La quantité à sortir
     * @return Un message d'erreur si le mouvement n'est pas possible, null sinon
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    public String verifierMouvementSortieAvecMessage(Long articleId, int quantiteSortie) throws SQLException {
        try {
            if (!verifierMouvementSortie(articleId, quantiteSortie)) {
                Article article = articleDAO.findById(articleId)
                    .orElseThrow(() -> new SQLException("Article non trouvé"));
                int stockActuel = calculateStockForArticle(articleId);
                return String.format("Stock insuffisant pour l'article %s. Stock actuel: %d, Quantité demandée: %d",
                    article.getNom(), stockActuel, quantiteSortie);
            }
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
} 