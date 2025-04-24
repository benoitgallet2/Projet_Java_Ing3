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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PagePrincipaleView {

    private final Client client;
    private VBox articlesBox;
    private List<Article> tousLesArticles;
    public static int compteurPanier = 0;
    public static final Label compteurPanierLabel = new Label("(0)");
    private final Map<Integer, Integer> quantitesAffichees = new HashMap<>();

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

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un article...");
        HBox searchBox = new HBox(10, searchField);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        articlesBox = new VBox(15);
        articlesBox.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(articlesBox);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(searchBox, scrollPane);
        BorderPane root = new BorderPane();
        root.setTop(navBar);
        root.setCenter(content);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Catalogue - Client connecté");
        stage.show();

        PanierDAO panierDAO = new PanierDAO();
        compteurPanier = panierDAO.getPanierByUser(client.getIdUser()).size();
        compteurPanierLabel.setText("(" + compteurPanier + ")");

        tousLesArticles = new ArticleDAO().findAll();
        Map<Integer, Integer> quantites = panierDAO.getQuantitesByUser(client.getIdUser());
        quantitesAffichees.putAll(quantites);

        afficherArticles(tousLesArticles);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Article> filtres = tousLesArticles.stream()
                    .filter(a -> a.getNomArticle().toLowerCase().startsWith(newVal.toLowerCase()))
                    .collect(Collectors.toList());
            afficherArticles(filtres);
        });
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
            articleInfo.getChildren().add(new Label("Stock : " + article.getQuantiteDispo()));

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
                Button plus = new Button("➕");
                Button moins = new Button("➖");

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
                interactionBox.getChildren().addAll(detailBtn, plus, qteLabel, moins);
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

