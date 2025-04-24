package view;

import dao.ArticleDAO;
import dao.PanierDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Article;
import model.Client;
import model.Panier;

import java.util.List;

public class PanierView {

    private final Client client;
    private final VBox panierBox = new VBox(10);
    private final Label totalLabel = new Label("Total : 0.00 €");

    public PanierView(Client client) {
        this.client = client;
    }

    public void start(Stage stage) {
        Label titre = new Label("Votre panier");
        titre.setFont(Font.font(20));

        ScrollPane scrollPane = new ScrollPane(panierBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        // 🔙 Retour à la page principale
        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> {
            PanierDAO panierDAO = new PanierDAO();
            int taille = panierDAO.getPanierByUser(client.getIdUser()).size();
            PagePrincipaleView.compteurPanier = taille;
            PagePrincipaleView.compteurPanierLabel.setText("(" + taille + ")");
            new PagePrincipaleView(client).start(stage);
        });

        // 🗑️ Bouton pour vider tout le panier
        Button viderBtn = new Button("Vider le panier");
        viderBtn.setOnAction(e -> {
            PanierDAO panierDAO = new PanierDAO();
            panierDAO.clearUserPanier(client.getIdUser());
            afficherPanier();

            PagePrincipaleView.compteurPanier = 0;
            PagePrincipaleView.compteurPanierLabel.setText("(0)");
        });

        // ✅ Bouton de validation
        Button validerBtn = new Button("Valider la commande");
        validerBtn.setOnAction(e -> {

        });

        HBox actions = new HBox(20, retourBtn, viderBtn, validerBtn);
        actions.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, titre, scrollPane, totalLabel, actions);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Panier - " + client.getPrenom());
        stage.show();

        afficherPanier();
    }

    private void afficherPanier() {
        panierBox.getChildren().clear();
        PanierDAO panierDAO = new PanierDAO();
        ArticleDAO articleDAO = new ArticleDAO();

        List<Panier> panier = panierDAO.getPanierByUser(client.getIdUser());
        double total = 0;

        for (Panier p : panier) {
            Article article = articleDAO.findById(p.getIdArticle());
            if (article == null) continue;

            HBox ligne = new HBox(15);
            ligne.setPadding(new Insets(10));
            ligne.setStyle("-fx-border-color: lightgray; -fx-background-radius: 10; -fx-border-radius: 10;");
            ligne.setAlignment(Pos.CENTER_LEFT);

            VBox info = new VBox(5);
            info.getChildren().add(new Label(article.getNomArticle() + " - " + article.getMarque()));
            info.getChildren().add(new Label("Prix : " + article.getPrixUnite() + " €"));

            double prix = article.getPrixUnite();
            total += prix;

            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.setOnAction(e -> {
                panierDAO.remove(new Panier(client.getIdUser(), article.getIdArticle()));
                afficherPanier();

                // Mise à jour du compteur
                int taille = panierDAO.getPanierByUser(client.getIdUser()).size();
                PagePrincipaleView.compteurPanier = taille;
                PagePrincipaleView.compteurPanierLabel.setText("(" + taille + ")");
            });

            ligne.getChildren().addAll(info, supprimerBtn);
            HBox.setHgrow(info, Priority.ALWAYS);
            panierBox.getChildren().add(ligne);
        }

        totalLabel.setText(String.format("Total : %.2f €", total));
    }
}
