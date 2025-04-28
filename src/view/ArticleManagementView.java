package view;

import dao.ArticleDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ArticleManagementView {

    public void start(Stage stage) {
        Text titre = new Text("🛠️ Gestion des articles");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Liste des articles
        VBox listeArticlesBox = new VBox(15);
        listeArticlesBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(listeArticlesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(700);

        // Recherche
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par nom ou ID...");

        TextField prixMaxField = new TextField();
        prixMaxField.setPromptText("Prix max");

        TextField marqueField = new TextField();
        marqueField.setPromptText("Marque contient");

        TextField noteMinField = new TextField();
        noteMinField.setPromptText("Note min");

        TextField quantiteMinField = new TextField();
        quantiteMinField.setPromptText("Quantité min");

        Button btnFiltrer = new Button("🔍 Filtrer");
        Button btnReset = new Button("♻️ Réinitialiser");

        HBox searchBox = new HBox(10, searchField);
        HBox filterBox = new HBox(10, prixMaxField, marqueField, noteMinField, quantiteMinField, btnFiltrer, btnReset);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        // Ajouter un article
        Button btnAjouter = new Button("Ajouter un article");
        btnAjouter.setPrefHeight(40);
        btnAjouter.setOnAction(e -> {
            try {
                new AjoutArticleView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox topRight = new HBox(btnAjouter);
        topRight.setAlignment(Pos.TOP_RIGHT);
        topRight.setPadding(new Insets(10, 0, 0, 0));

        VBox leftContent = new VBox(10, scrollPane);
        leftContent.setPrefWidth(1000);
        HBox mainLayout = new HBox(20, leftContent, topRight);
        HBox.setHgrow(leftContent, Priority.ALWAYS);

        // 🔙 Bouton retour
        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> {
            try {
                new AdminView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox retourBox = new HBox(btnRetour);
        retourBox.setAlignment(Pos.BOTTOM_RIGHT);
        retourBox.setPadding(new Insets(0, 20, 10, 0));

        VBox root = new VBox(20, titre, searchBox, filterBox, mainLayout, retourBox);
        root.setPadding(new Insets(20));

        // Articles
        List<Article> allArticles = new ArticleDAO().findAll();
        afficherArticles(listeArticlesBox, allArticles, stage);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Article> result = filtrerArticles(allArticles, newVal, prixMaxField, marqueField, noteMinField, quantiteMinField);
            afficherArticles(listeArticlesBox, result, stage);
        });

        btnFiltrer.setOnAction(e -> {
            List<Article> result = filtrerArticles(allArticles, searchField.getText(), prixMaxField, marqueField, noteMinField, quantiteMinField);
            afficherArticles(listeArticlesBox, result, stage);
        });

        btnReset.setOnAction(e -> {
            searchField.clear();
            prixMaxField.clear();
            marqueField.clear();
            noteMinField.clear();
            quantiteMinField.clear();
            afficherArticles(listeArticlesBox, allArticles, stage);
        });

        Scene scene = new Scene(root, 1300, 800);
        stage.setScene(scene);
        stage.setTitle("Gestion des articles");
        stage.show();
    }

    // ✅ autres méthodes inchangées (filtrerArticles et afficherArticles)

    private List<Article> filtrerArticles(List<Article> articles, String search, TextField prixMax, TextField marque, TextField noteMin, TextField quantiteMin) {
        String s = search.toLowerCase().trim();
        List<Article> filtered = new ArrayList<>();

        for (Article a : articles) {
            boolean match = true;

            if (!s.isEmpty() && !(a.getNomArticle().toLowerCase().contains(s) || String.valueOf(a.getIdArticle()).equals(s))) {
                match = false;
            }

            if (!prixMax.getText().isEmpty()) {
                try {
                    double max = Double.parseDouble(prixMax.getText());
                    if (a.getPrixUnite() > max) match = false;
                } catch (NumberFormatException ignored) {}
            }

            if (!marque.getText().isEmpty() && !a.getMarque().toLowerCase().contains(marque.getText().toLowerCase())) {
                match = false;
            }

            if (!noteMin.getText().isEmpty()) {
                try {
                    int min = Integer.parseInt(noteMin.getText());
                    if (a.getNote() < min) match = false;
                } catch (NumberFormatException ignored) {}
            }

            if (!quantiteMin.getText().isEmpty()) {
                try {
                    double min = Double.parseDouble(quantiteMin.getText());
                    if (a.getQuantiteDispo() < min) match = false;
                } catch (NumberFormatException ignored) {}
            }

            if (match) filtered.add(a);
        }

        return filtered;
    }

    private void afficherArticles(VBox container, List<Article> articles, Stage stage) {
        container.getChildren().clear();

        for (Article article : articles) {
            HBox articleBox = new HBox(10);
            articleBox.setPadding(new Insets(10));
            articleBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px;");
            articleBox.setPrefWidth(1000);
            articleBox.setMaxWidth(Double.MAX_VALUE);

            ImageView imagePreview = new ImageView();
            try {
                if (article.getImageBytes() != null) {
                    Image image = new Image(new ByteArrayInputStream(article.getImageBytes()));
                    imagePreview.setImage(image);
                    imagePreview.setFitHeight(60);
                    imagePreview.setPreserveRatio(true);
                }
            } catch (Exception ignored) {}

            Label nom = new Label(article.getNomArticle());
            nom.setWrapText(true);
            nom.setPrefWidth(350);
            nom.setMaxWidth(350);
            nom.setStyle("-fx-font-size: 16px;");

            Button btnDetail = new Button("Détail");
            Button btnModifier = new Button("Modifier");
            Button btnSupprimer = new Button("Supprimer");

            btnDetail.setOnAction(e -> new DetailArticleView(article).start(stage));
            btnModifier.setOnAction(e -> new ModifierArticleView(article).start(stage));
            btnSupprimer.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Voulez-vous vraiment supprimer cet article ?");
                alert.setContentText(article.getNomArticle());
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean deleted = new ArticleDAO().delete(article.getIdArticle());
                        if (deleted) {
                            new ArticleManagementView().start(stage);
                        }
                    }
                });
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            articleBox.getChildren().addAll(imagePreview, nom, spacer, btnDetail, btnModifier, btnSupprimer);
            container.getChildren().add(articleBox);
        }
    }
}
