package view;

import dao.ArticleDAO;
import dao.LigneCommandeDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;
import model.Commande;
import model.Compte;
import model.LigneCommande;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailCommandeView {

    private final Commande commande;
    private final Compte compte;
    private final Runnable onRetour;

    public DetailCommandeView(Commande commande, Compte compte, Runnable onRetour) {
        this.commande = commande;
        this.compte = compte;
        this.onRetour = onRetour;
    }

    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.TOP_LEFT);

        int row = 0;

        grid.add(new Text("🧾 Détail de la commande #" + commande.getIdCommande()), 0, row++, 2, 1);

        grid.add(new Text("Montant : "), 0, row);
        grid.add(new Text(commande.getMontant() + " €"), 1, row++);

        grid.add(new Text("Date : "), 0, row);
        grid.add(new Text(commande.getDate().toString()), 1, row++);

        grid.add(new Text("Statut : "), 0, row);
        grid.add(new Text(commande.getStatut()), 1, row++);

        grid.add(new Text("Note client : "), 0, row);
        grid.add(new Text(commande.getNoteClient() != null ? String.valueOf(commande.getNoteClient()) : "Aucune"), 1, row++);

        // 🔍 Articles commandés avec quantités regroupées
        LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
        List<LigneCommande> lignes = ligneDAO.findByCommandeId(commande.getIdCommande());

        ArticleDAO articleDAO = new ArticleDAO();
        Map<Integer, Integer> quantites = new HashMap<>();

        for (LigneCommande ligne : lignes) {
            int articleId = ligne.getIdArticle();
            quantites.put(articleId, quantites.getOrDefault(articleId, 0) + 1);
        }

        if (!quantites.isEmpty()) {
            grid.add(new Text("Articles :"), 0, row++);
            for (int articleId : quantites.keySet()) {
                Article article = articleDAO.findById(articleId);
                String nom = (article != null) ? article.getNomArticle() : "Article introuvable";
                int qte = quantites.get(articleId);
                grid.add(new Text("- " + nom + " (ID: " + articleId + ") : " + qte), 1, row++);
            }
        } else {
            grid.add(new Text("Aucun article lié à cette commande."), 0, row++, 2, 1);
        }

        // 🔙 Bouton retour vers la vue d'origine
        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> onRetour.run());
        grid.add(btnRetour, 1, row);

        stage.setScene(new Scene(grid, 600, 500));
        stage.setTitle("Commande #" + commande.getIdCommande());
        stage.show();
    }
}
