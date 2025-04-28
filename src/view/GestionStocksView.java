package view;

import dao.ArticleDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Article;

import java.util.List;
import java.util.stream.Collectors;

public class GestionStocksView {

    private VBox articlesBox;
    private List<Article> allArticles;
    private ArticleDAO dao = new ArticleDAO();

    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label titre = new Label("📦 Gestion des stocks");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par nom ou marque...");

        CheckBox onlyLowStock = new CheckBox("Afficher uniquement les stocks faibles (< 10)");

        HBox filters = new HBox(10, searchField, onlyLowStock);
        filters.setAlignment(Pos.CENTER_LEFT);

        articlesBox = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(articlesBox);
        scrollPane.setFitToWidth(true);

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> new AdminView().start(stage));

        root.getChildren().addAll(titre, filters, scrollPane, retourBtn);

        allArticles = dao.findAll();
        afficherArticles(allArticles);

        // 🔁 Filtres dynamiques
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFiltrage(searchField.getText(), onlyLowStock.isSelected()));
        onlyLowStock.setOnAction(e -> updateFiltrage(searchField.getText(), onlyLowStock.isSelected()));

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Gestion des stocks");
        stage.setScene(scene);
        stage.show();
    }

    private void updateFiltrage(String search, boolean lowStockOnly) {
        String lower = search.toLowerCase();
        List<Article> filtrés = allArticles.stream()
                .filter(a -> a.getNomArticle().toLowerCase().contains(lower)
                        || a.getMarque().toLowerCase().contains(lower))
                .filter(a -> !lowStockOnly || a.getQuantiteDispo() < 10)
                .collect(Collectors.toList());
        afficherArticles(filtrés);
    }

    private void afficherArticles(List<Article> articles) {
        articlesBox.getChildren().clear();

        for (Article article : articles) {
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(10);
            grid.setVgap(5);
            grid.setStyle("-fx-border-color: lightgray; -fx-border-radius: 8px;");

            int row = 0;
            Label nom = new Label("🛒 " + article.getNomArticle() + " - " + article.getMarque());
            nom.setStyle("-fx-font-weight: bold;");
            grid.add(nom, 0, row++, 2, 1);

            grid.add(new Label("Stock actuel : "), 0, row);
            TextField qteField = new TextField(String.valueOf(article.getQuantiteDispo()));
            grid.add(qteField, 1, row++);

            Button saveBtn = new Button("Enregistrer");
            Label feedback = new Label();

            saveBtn.setOnAction(e -> {
                try {
                    double newQte = Double.parseDouble(qteField.getText());
                    article.setQuantiteDispo(newQte);
                    boolean ok = dao.update(article);
                    feedback.setText(ok ? "✅ Mis à jour !" : "❌ Erreur.");
                } catch (Exception ex) {
                    feedback.setText("⚠️ Saisie invalide.");
                }
            });

            // Style visuel si stock critique
            double stock = article.getQuantiteDispo();
            if (stock < 5) {
                grid.setStyle(grid.getStyle() + "-fx-background-color: #ffe6e6;");
            } else if (stock < 10) {
                grid.setStyle(grid.getStyle() + "-fx-background-color: #fff8e1;");
            }

            grid.add(saveBtn, 0, row);
            grid.add(feedback, 1, row++);

            articlesBox.getChildren().add(grid);
        }
    }
}
