package view;

import dao.ArticleDAO;
import dao.CommandeDAO;
import dao.CompteDAO;
import dao.LigneCommandeDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vue JavaFX permettant d'afficher des statistiques globales sur les comptes,
 * les commandes, les ventes et les articles les plus commandés.
 */
public class StatistiquesView {

    /**
     * Démarre l'affichage de la vue de statistiques globales.
     *
     * @param stage La fenêtre JavaFX où s'affiche la vue.
     */
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setVgap(15);
        grid.setHgap(20);
        grid.setAlignment(Pos.TOP_LEFT);

        int row = 0;
        grid.add(new Text("📊 Statistiques globales"), 0, row++, 2, 1);

        CompteDAO compteDAO = new CompteDAO();
        CommandeDAO commandeDAO = new CommandeDAO();
        LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
        ArticleDAO articleDAO = new ArticleDAO();

        int nbComptes = compteDAO.findAll().size();
        int nbClients = (int) compteDAO.findAll().stream().filter(c -> !c.isAdmin()).count();
        int nbCommandes = commandeDAO.findAll().size();
        double totalVentes = commandeDAO.findAll().stream().mapToDouble(c -> c.getMontant()).sum();

        List<Integer> articlesCommandes = ligneDAO.getAllArticleIds();
        Map<Integer, Integer> compteur = new HashMap<>();
        for (int id : articlesCommandes) {
            compteur.put(id, compteur.getOrDefault(id, 0) + 1);
        }

        int maxArticleId = compteur.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);

        Article articleTop = articleDAO.findById(maxArticleId);
        String topNom = (articleTop != null) ? articleTop.getNomArticle() : "Aucun";
        int topNb = compteur.getOrDefault(maxArticleId, 0);

        grid.add(new Text("👤 Comptes totaux : "), 0, row);
        grid.add(new Text(String.valueOf(nbComptes)), 1, row++);

        grid.add(new Text("👥 Clients : "), 0, row);
        grid.add(new Text(String.valueOf(nbClients)), 1, row++);

        grid.add(new Text("📦 Commandes : "), 0, row);
        grid.add(new Text(String.valueOf(nbCommandes)), 1, row++);

        grid.add(new Text("💰 Total des ventes : "), 0, row);
        grid.add(new Text(String.format("%.2f €", totalVentes)), 1, row++);

        grid.add(new Text("🏆 Article le plus commandé : "), 0, row);
        grid.add(new Text(topNom + " (" + topNb + " fois)"), 1, row++);

        Button avanceesBtn = new Button("📊 Statistiques avancées");
        avanceesBtn.setOnAction(e -> new StatistiquesAvanceesView().start(stage));
        grid.add(avanceesBtn, 0, row++, 2, 1);

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> new AdminView().start(stage));
        grid.add(retourBtn, 1, row);

        Scene scene = new Scene(grid, 500, 400);
        stage.setScene(scene);
        stage.setTitle("Statistiques globales");
        stage.show();
    }
}
