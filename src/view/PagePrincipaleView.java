package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Client;

public class PagePrincipaleView {

    private Client client;

    public PagePrincipaleView(Client client) {
        this.client = client;
    }

    public void start(Stage stage) {
        // Titre centré
        Label titreLabel = new Label("Les Bogoss");
        titreLabel.setFont(Font.font(20));

        // Menu déroulant à droite
        MenuItem profilItem = new MenuItem("Profil");
        MenuItem commandesItem = new MenuItem("Commandes");
        MenuItem deconnexionItem = new MenuItem("Déconnexion");
        MenuButton menuButton = new MenuButton("Bonjour, " + client.getPrenom() + " !", null, profilItem, commandesItem, deconnexionItem);
        menuButton.setFont(Font.font(14));

        // Panier à droite
        Button panierBtn = new Button("🛒");
        panierBtn.setFont(Font.font(16));

        // Espaces pour alignement
        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        HBox navBar = new HBox(20, menuButton, spacerLeft, titreLabel, spacerRight, panierBtn);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(15));
        navBar.setStyle("-fx-background-color: #f0f0f0;");

        VBox articlesBox = new VBox(15);
        articlesBox.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(articlesBox);
        scrollPane.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setTop(navBar);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Catalogue - Client connecté");
        stage.show();
    }
}
