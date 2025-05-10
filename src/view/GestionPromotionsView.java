package view;

import dao.ArticleDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vue pour la gestion des promotions des articles.
 * Permet aux administrateurs de modifier les prix en vrac et les modulos de réduction.
 */
public class GestionPromotionsView {

    private VBox articlesBox;
    private List<Article> allArticles;

    /**
     * Affiche la vue de gestion des promotions.
     *
     * @param stage la fenêtre JavaFX courante
     */
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label titre = new Label("🎯 Gestion des Promotions");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un article par nom ou marque...");
        HBox searchBox = new HBox(searchField);
        searchBox.setPadding(new Insets(0, 0, 10, 0));

        articlesBox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(articlesBox);
        scrollPane.setFitToWidth(true);

        ArticleDAO dao = new ArticleDAO();
        allArticles = dao.findAll();
        afficherArticles(allArticles, dao);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal.toLowerCase();
            List<Article> filtres = allArticles.stream()
                    .filter(a -> a.getNomArticle().toLowerCase().contains(lower)
                            || a.getMarque().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
            afficherArticles(filtres, dao);
        });

        Button retour = new Button("Retour");
        retour.setOnAction(e -> new AdminView().start(stage));

        root.getChildren().addAll(titre, searchBox, scrollPane, retour);

        Scene scene = new Scene(root, 750, 600);
        stage.setTitle("Gestion des promotions");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Affiche les articles sous forme de panneaux avec leurs options de promotion.
     *
     * @param articles la liste des articles à afficher
     * @param dao      l’objet DAO pour enregistrer les modifications
     */
    private void afficherArticles(List<Article> articles, ArticleDAO dao) {
        articlesBox.getChildren().clear();

        for (Article article : articles) {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10));
            grid.setStyle("-fx-border-color: lightgray; -fx-border-radius: 8px;");

            int row = 0;
            grid.add(new Label("🛒 " + article.getNomArticle() + " - " + article.getMarque()), 0, row++, 2, 1);
            grid.add(new Label("Prix unité : " + article.getPrixUnite() + " €"), 0, row++, 2, 1);

            TextField modField = new TextField(String.valueOf(article.getModuloReduction()));
            TextField vracField = new TextField(String.valueOf(article.getPrixVrac()));
            TextField quantiteField = new TextField();
            quantiteField.setPromptText("Quantité");
            Label simulation = new Label();

            grid.add(new Label("Modulo réduction : "), 0, row);
            grid.add(modField, 1, row++);

            grid.add(new Label("Prix vrac : "), 0, row);
            grid.add(vracField, 1, row++);

            grid.add(new Label("Simulation pour : "), 0, row);
            grid.add(quantiteField, 1, row++);

            Button btnCalculer = new Button("Calculer montant");
            Button btnAppliquer = new Button("Appliquer promo");

            HBox actions = new HBox(10, btnCalculer, btnAppliquer);
            grid.add(actions, 0, row++, 2, 1);
            grid.add(simulation, 0, row++, 2, 1);

            /**
             * Gère la simulation de promotion selon la quantité saisie.
             */
            btnCalculer.setOnAction(e -> {
                try {
                    int qte = Integer.parseInt(quantiteField.getText());
                    double prixUnite = article.getPrixUnite();
                    int modulo = Integer.parseInt(modField.getText());
                    double prixVrac = Double.parseDouble(vracField.getText());

                    int lots = qte / modulo;
                    int restants = qte % modulo;
                    double total = lots * modulo * prixVrac + restants * prixUnite;

                    simulation.setText("💰 Montant estimé : " + String.format("%.2f", total) + " €");
                } catch (Exception ex) {
                    simulation.setText("Erreur de saisie.");
                }
            });

            /**
             * Applique la promotion au produit sélectionné.
             */
            btnAppliquer.setOnAction(e -> {
                try {
                    article.setModuloReduction(Integer.parseInt(modField.getText()));
                    article.setPrixVrac(Double.parseDouble(vracField.getText()));

                    boolean ok = dao.update(article);
                    if (ok) simulation.setText("Promotion enregistrée !");
                    else simulation.setText("Échec de la mise à jour.");
                } catch (Exception ex) {
                    simulation.setText("Erreur de saisie.");
                }
            });

            articlesBox.getChildren().add(grid);
        }
    }
}
