package view;

import dao.ArticleDAO;
import dao.ClientDAO;
import dao.CommandeDAO;
import dao.PanierDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;
import model.Client;
import model.Commande;
import model.Panier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vue du profil client affichant les informations personnelles et le contenu actuel du panier.
 */
public class ProfilView {

    private final Client client;

    /**
     * Constructeur de la vue du profil.
     *
     * @param client le client connecté
     */
    public ProfilView(Client client) {
        this.client = client;
    }

    /**
     * Démarre l'affichage de la vue du profil utilisateur.
     *
     * @param stage la fenêtre actuelle
     */
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.TOP_LEFT);

        int row = 0;

        // Informations de compte
        grid.add(new Text("Login : "), 0, row);
        grid.add(new Text(client.getLogin()), 1, row++);

        grid.add(new Text("Mot de passe : "), 0, row);
        grid.add(new Text("********"), 1, row++); // Masqué volontairement

        grid.add(new Text("Nom : "), 0, row);
        grid.add(new Text(client.getNom()), 1, row++);

        grid.add(new Text("Prénom : "), 0, row);
        grid.add(new Text(client.getPrenom()), 1, row++);

        // Affichage du panier actuel
        PanierDAO panierDAO = new PanierDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        List<Panier> panierList = panierDAO.getPanierByUser(client.getIdUser());

        Map<Integer, Integer> quantitesPanier = new HashMap<>();
        for (Panier p : panierList) {
            int articleId = p.getIdArticle();
            quantitesPanier.put(articleId, quantitesPanier.getOrDefault(articleId, 0) + 1);
        }

        if (!quantitesPanier.isEmpty()) {
            grid.add(new Text("Panier actuel :"), 0, row++);
            for (int articleId : quantitesPanier.keySet()) {
                Article a = articleDAO.findById(articleId);
                String nom = (a != null) ? a.getNomArticle() : "Article introuvable";
                int qte = quantitesPanier.get(articleId);
                grid.add(new Text("- " + nom + " (ID: " + articleId + ") : " + qte), 1, row++);
            }
        } else {
            grid.add(new Text("Panier vide."), 0, row++, 2, 1);
        }

        // Modifier profil
        Button btnModifier = new Button("Modifier mes informations");
        btnModifier.setOnAction(e -> {
            new ModifierProfilView(client).show(stage);
            new ProfilView(client).start(stage); // rafraîchir la vue
        });
        grid.add(btnModifier, 1, row++);

        // Retour à la page principale
        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> new PagePrincipaleView(client).start(stage));
        grid.add(btnRetour, 1, row++);

        stage.setScene(new Scene(grid, 650, 600));
        stage.setTitle("Mon profil");
        stage.show();
    }
}
