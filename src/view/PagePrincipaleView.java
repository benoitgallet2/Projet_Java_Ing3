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
import java.util.List;
import java.util.stream.Collectors;

public class PagePrincipaleView {

    private final Client client;
    private VBox articlesBox;
    private List<Article> tousLesArticles;

    // compteur statique
    public static int compteurPanier = 0;
    public static final Label compteurPanierLabel = new Label("(0)");

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

        // Panier avec compteur
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

        tousLesArticles = new ArticleDAO().findAll();

        // 🟢 compteur à jour dès le chargement
        compteurPanier = new PanierDAO().getPanierByUser(client.getIdUser()).size();
        compteurPanierLabel.setText("(" + compteurPanier + ")");

        afficherArticles(tousLesArticles);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String search = newValue.toLowerCase();
            List<Article> filtres = tousLesArticles.stream()
                    .filter(a -> a.getNomArticle().toLowerCase().startsWith(search))
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

        for (Article article : articles) {
            ImageView imageView = new ImageView();
            byte[] imageData = article.getImageBytes();

            if (imageData != null && imageData.length > 0) {
                Image image = new Image(new ByteArrayInputStream(imageData));
                imageView.setImage(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
            }

            VBox articleInfo = new VBox(5);
            articleInfo.getChildren().add(new Label(article.getNomArticle() + " - " + article.getMarque()));
            articleInfo.getChildren().add(new Label("Prix unité : " + article.getPrixUnite() + " €"));
            if (article.getPrixVrac() != null && article.getPrixVrac() > 0) {
                articleInfo.getChildren().add(new Label("Prix vrac : " + article.getPrixVrac() + " €"));
            }
            articleInfo.getChildren().add(new Label("Quantité disponible : " + article.getQuantiteDispo()));

            Button detailBtn = new Button("Détail");
            Button ajouterBtn = new Button("Ajouter");
            detailBtn.setFont(Font.font(12));
            ajouterBtn.setFont(Font.font(12));

            detailBtn.setOnAction(e -> afficherPopupDetail(article));
            ajouterBtn.setOnAction(e -> {
                PanierDAO panierDAO = new PanierDAO();
                Panier p = new Panier(client.getIdUser(), article.getIdArticle());
                panierDAO.add(p);

                compteurPanier++;
                compteurPanierLabel.setText("(" + compteurPanier + ")");
            });

            HBox actionsBox = new HBox(10, detailBtn, ajouterBtn);
            actionsBox.setAlignment(Pos.CENTER_RIGHT);

            HBox articleBox = new HBox(20, imageView, articleInfo, actionsBox);
            articleBox.setPadding(new Insets(10));
            articleBox.setStyle("-fx-border-color: lightgray; -fx-background-radius: 10; -fx-border-radius: 10;");
            articleBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(articleInfo, Priority.ALWAYS);

            articlesBox.getChildren().add(articleBox);
        }
    }
}
