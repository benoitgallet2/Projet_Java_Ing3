package view;

import dao.ArticleDAO;
import dao.PanierDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Article;
import model.Client;
import model.Panier;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PagePrincipaleView {

    private final Client client;
    private VBox articlesBox;
    private List<Article> tousLesArticles;
    public static int compteurPanier = 0;
    public static final Label compteurPanierLabel = new Label("(0)");
    private final Map<Integer, Integer> quantitesAffichees = new java.util.HashMap<>();

    private TextField searchField;
    private TextField prixMaxField;
    private TextField marqueField;
    private TextField noteMinField;
    private TextField quantiteMinField;

    public PagePrincipaleView(Client client) {
        this.client = client;
    }

    public void start(Stage stage) {
        Label titreLabel = new Label("Les Bogoss");
        titreLabel.setFont(Font.font(20));

        MenuItem profilItem = new MenuItem("Profil");
        MenuItem commandesItem = new MenuItem("Commandes");
        MenuItem deconnexionItem = new MenuItem("Déconnexion");
        MenuButton menuButton = new MenuButton("Bonjour, " + client.getPrenom() + " !", null, profilItem, commandesItem, deconnexionItem);
        menuButton.setFont(Font.font(14));
        deconnexionItem.setOnAction(e -> new ConnexionView().start(stage));
        profilItem.setOnAction(e -> new ProfilView(client).start(stage));

        Button panierBtn = new Button("🛒");
        panierBtn.setFont(Font.font(16));
        compteurPanierLabel.setFont(Font.font(14));
        HBox panierBox = new HBox(5, panierBtn, compteurPanierLabel);
        panierBox.setAlignment(Pos.CENTER_RIGHT);

        panierBtn.setOnAction(e -> new PanierView(client).start(stage));

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        HBox navBar = new HBox(20, menuButton, spacerLeft, titreLabel, spacerRight, panierBox);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(15));
        navBar.setStyle("-fx-background-color: #f0f0f0;");

        // Champs de recherche
        searchField = new TextField();
        searchField.setPromptText("Rechercher un article...");

        prixMaxField = new TextField();
        prixMaxField.setPromptText("Prix max");

        marqueField = new TextField();
        marqueField.setPromptText("Marque contient");

        noteMinField = new TextField();
        noteMinField.setPromptText("Note min");

        quantiteMinField = new TextField();
        quantiteMinField.setPromptText("Quantité min");

        Button btnFiltrer = new Button("🔍 Filtrer");
        Button btnReset = new Button("♻️ Réinitialiser");

        HBox searchBox = new HBox(10, searchField);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        HBox filterBox = new HBox(10, prixMaxField, marqueField, noteMinField, quantiteMinField, btnFiltrer, btnReset);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setPadding(new Insets(5));

        articlesBox = new VBox(15);
        articlesBox.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(articlesBox);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(searchBox, filterBox, scrollPane);
        BorderPane root = new BorderPane();
        root.setTop(navBar);
        root.setCenter(content);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Catalogue - Client connecté");
        stage.show();

        PanierDAO panierDAO = new PanierDAO();
        compteurPanier = panierDAO.getPanierByUser(client.getIdUser()).size();
        compteurPanierLabel.setText("(" + compteurPanier + ")");

        tousLesArticles = new ArticleDAO().findAll();
        Map<Integer, Integer> quantites = panierDAO.getPanierByUser(client.getIdUser()).stream()
                .collect(Collectors.groupingBy(Panier::getIdArticle, Collectors.summingInt(e -> 1)));
        quantitesAffichees.putAll(quantites);

        afficherArticles(tousLesArticles);

        // Recherche dynamique uniquement sur searchField
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Article> filtrés = tousLesArticles.stream()
                    .filter(a -> a.getNomArticle().toLowerCase().contains(newVal.toLowerCase()) || String.valueOf(a.getIdArticle()).contains(newVal))
                    .collect(Collectors.toList());
            afficherArticles(filtrés);
        });

        // Action filtrer et reset
        btnFiltrer.setOnAction(e -> {
            List<Article> filtres = filtrerArticles();
            afficherArticles(filtres);
        });

        btnReset.setOnAction(e -> {
            searchField.clear();
            prixMaxField.clear();
            marqueField.clear();
            noteMinField.clear();
            quantiteMinField.clear();
            afficherArticles(tousLesArticles);
        });
    }

    private List<Article> filtrerArticles() {
        List<Article> filtered = new ArrayList<>();

        for (Article a : tousLesArticles) {
            boolean match = true;

            if (!prixMaxField.getText().isEmpty()) {
                try {
                    double max = Double.parseDouble(prixMaxField.getText());
                    if (a.getPrixUnite() > max) match = false;
                } catch (NumberFormatException ignored) {}
            }

            if (!marqueField.getText().isEmpty() && !a.getMarque().toLowerCase().contains(marqueField.getText().toLowerCase())) {
                match = false;
            }

            if (!noteMinField.getText().isEmpty()) {
                try {
                    int min = Integer.parseInt(noteMinField.getText());
                    if (a.getNote() < min) match = false;
                } catch (NumberFormatException ignored) {}
            }

            if (!quantiteMinField.getText().isEmpty()) {
                try {
                    double min = Double.parseDouble(quantiteMinField.getText());
                    if (a.getQuantiteDispo() < min) match = false;
                } catch (NumberFormatException ignored) {}
            }

            if (match) filtered.add(a);
        }

        return filtered;
    }
    private void afficherPopupDetail(Article article) {
        Stage popup = new Stage();
        popup.initOwner(articlesBox.getScene().getWindow());
        popup.setTitle("Détail - " + article.getNomArticle());

        VBox contenu = new VBox(10);
        contenu.setPadding(new Insets(20));
        contenu.setAlignment(Pos.CENTER);

        Label titre = new Label(article.getNomArticle() + " - " + article.getMarque());
        titre.setFont(Font.font(18));

        Label description = new Label("Description : " + article.getDescription());
        description.setWrapText(true);

        Label note = new Label("Note : " + article.getNote() + " / 5");

        ImageView imageView = new ImageView();
        byte[] imageData = article.getImageBytes();
        if (imageData != null && imageData.length > 0) {
            Image image = new Image(new ByteArrayInputStream(imageData));
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
        }

        Button fermerBtn = new Button("Fermer");
        fermerBtn.setOnAction(e -> popup.close());

        contenu.getChildren().addAll(titre, imageView, description, note, fermerBtn);

        Scene scene = new Scene(contenu, 350, 400);
        popup.setScene(scene);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.show();
    }

    private void afficherArticles(List<Article> articles) {
        articlesBox.getChildren().clear();
        PanierDAO panierDAO = new PanierDAO();

        for (Article article : articles) {
            VBox articleInfo = new VBox(5);
            articleInfo.getChildren().add(new Label(article.getNomArticle() + " - " + article.getMarque()));
            articleInfo.getChildren().add(new Label("Prix unité : " + article.getPrixUnite() + " €"));

            if (article.getPrixVrac() != null && article.getModuloReduction() > 0) {
                articleInfo.getChildren().add(new Label("Prix réduit : " + article.getPrixVrac() + " € (à partir de " + article.getModuloReduction() + " unités)"));
            }

            articleInfo.getChildren().add(new Label("Quantité disponible : " + article.getQuantiteDispo()));

            ImageView imageView = new ImageView();
            byte[] imageData = article.getImageBytes();
            if (imageData != null && imageData.length > 0) {
                Image image = new Image(new ByteArrayInputStream(imageData));
                imageView.setImage(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
            }

            Button ajouterBtn = new Button("Ajouter");
            Button detailBtn = new Button("Détail");
            ajouterBtn.setFont(Font.font(12));
            detailBtn.setFont(Font.font(12));

            HBox interactionBox = new HBox(10, detailBtn, ajouterBtn);
            interactionBox.setAlignment(Pos.CENTER_RIGHT);

            VBox globalBox = new VBox(5, articleInfo, interactionBox);
            globalBox.setPadding(new Insets(10));

            detailBtn.setOnAction(e -> afficherPopupDetail(article));
            ajouterBtn.setOnAction(e -> {
                panierDAO.add(new Panier(client.getIdUser(), article.getIdArticle()));
                compteurPanier++;
                compteurPanierLabel.setText("(" + compteurPanier + ")");
                quantitesAffichees.put(article.getIdArticle(), quantitesAffichees.getOrDefault(article.getIdArticle(), 0) + 1);
                afficherArticles(tousLesArticles);
            });

            if (quantitesAffichees.containsKey(article.getIdArticle())) {
                int qte = quantitesAffichees.get(article.getIdArticle());
                Label qteLabel = new Label("Quantité : " + qte);
                Button moins = new Button("➖");
                Button plus = new Button("➕");

                plus.setOnAction(e -> {
                    panierDAO.add(new Panier(client.getIdUser(), article.getIdArticle()));
                    compteurPanier++;
                    quantitesAffichees.put(article.getIdArticle(), qte + 1);
                    compteurPanierLabel.setText("(" + compteurPanier + ")");
                    afficherArticles(tousLesArticles);
                });

                moins.setOnAction(e -> {
                    panierDAO.remove(new Panier(client.getIdUser(), article.getIdArticle()));
                    compteurPanier--;
                    if (qte - 1 <= 0) {
                        quantitesAffichees.remove(article.getIdArticle());
                    } else {
                        quantitesAffichees.put(article.getIdArticle(), qte - 1);
                    }
                    compteurPanierLabel.setText("(" + compteurPanier + ")");
                    afficherArticles(tousLesArticles);
                });

                interactionBox.getChildren().clear();
                interactionBox.getChildren().addAll(detailBtn, moins, qteLabel, plus);
            }

            HBox articleBox = new HBox(20, imageView, globalBox);
            articleBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 10; -fx-background-radius: 10;");
            articleBox.setPadding(new Insets(10));
            articleBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(globalBox, Priority.ALWAYS);

            articlesBox.getChildren().add(articleBox);
        }
    }
}
