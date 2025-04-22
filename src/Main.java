import java.sql.Connection;

import utils.DBConnection;
import dao.ArticleDAO;
import model.Article;

public class Main {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        Article article = new Article(
                "Stylo bleu",
                1.20,
                10.0,        // Prix en vrac
                10,          // Réduction dès 10 pièces
                "Bic",       // Marque
                100,         // Quantité disponible
                null,        // Pas d'image ici
                5,           // Note
                "Stylo à bille bleu, écriture fluide" // Description
        );
        ArticleDAO dao = new ArticleDAO();
        boolean success = dao.create(article);

        if (success) {
            System.out.println("✅ Article inséré avec succès !");
        } else {
            System.out.println("❌ Échec de l'insertion de l'article.");
        }
    }
}