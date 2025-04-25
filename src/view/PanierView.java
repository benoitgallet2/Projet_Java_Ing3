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
import java.util.Map;

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

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> {
            PanierDAO panierDAO = new PanierDAO();
            int taille = panierDAO.getPanierByUser(client.getIdUser()).size();
            PagePrincipaleView.compteurPanier = taille;
            PagePrincipaleView.compteurPanierLabel.setText("(" + taille + ")");
            new PagePrincipaleView(client).start(stage);
        });

        Button viderBtn = new Button("Vider le panier");
        viderBtn.setOnAction(e -> {
            PanierDAO panierDAO = new PanierDAO();
            panierDAO.clearUserPanier(client.getIdUser());
            PagePrincipaleView.compteurPanier = 0;
            PagePrincipaleView.compteurPanierLabel.setText("(0)");
            afficherPanier();
        });

        Button validerBtn = new Button("Valider la commande");
        validerBtn.setOnAction(e -> {
            // À compléter plus tard
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

        Map<Integer, Integer> quantites = panierDAO.getQuantitesByUser(client.getIdUser());
        double totalGeneral = 0;

        for (Map.Entry<Integer, Integer> entry : quantites.entrySet()) {
            int idArticle = entry.getKey();
            int quantite = entry.getValue();

            Article article = articleDAO.findById(idArticle);
            if (article == null) continue;

            VBox info = new VBox(5);
            info.getChildren().add(new Label(article.getNomArticle() + " - " + article.getMarque()));
            info.getChildren().add(new Label("Prix unitaire : " + article.getPrixUnite() + " €"));
            info.getChildren().add(new Label("Quantité : " + quantite));

            if (article.getPrixVrac() != null && article.getModuloReduction() > 0
                    && quantite >= article.getModuloReduction()) {
                Label prixVracApplique = new Label("Prix vrac appliqué");
                prixVracApplique.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                info.getChildren().add(prixVracApplique);
            }

            double prixSansReduction = quantite * article.getPrixUnite();
            double totalLigne;
            boolean reductionActive = false;

            if (article.getPrixVrac() != null && article.getModuloReduction() > 0) {
                int packs = quantite / article.getModuloReduction();
                int restes = quantite % article.getModuloReduction();
                totalLigne = (packs * article.getPrixVrac()) + (restes * article.getPrixUnite());
                reductionActive = packs > 0;
            } else {
                totalLigne = prixSansReduction;
            }

            if (reductionActive && prixSansReduction > totalLigne) {
                Label promo = new Label(
                        String.format("%.2f", prixSansReduction) + " € ➝ " + String.format("%.2f", totalLigne) + " €"
                );
                promo.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
                info.getChildren().add(promo);
            } else {
                info.getChildren().add(new Label("Total : " + String.format("%.2f", totalLigne) + " €"));
            }

            totalGeneral += totalLigne;

            Button plusBtn = new Button("➕");
            Button moinsBtn = new Button("➖");
            Button supprimerBtn = new Button("Supprimer");

            plusBtn.setOnAction(e -> {
                panierDAO.add(new Panier(client.getIdUser(), idArticle));
                PagePrincipaleView.compteurPanier++;
                PagePrincipaleView.compteurPanierLabel.setText("(" + PagePrincipaleView.compteurPanier + ")");
                afficherPanier();
            });

            moinsBtn.setOnAction(e -> {
                panierDAO.remove(new Panier(client.getIdUser(), idArticle));
                PagePrincipaleView.compteurPanier--;
                PagePrincipaleView.compteurPanierLabel.setText("(" + PagePrincipaleView.compteurPanier + ")");
                afficherPanier();
            });

            supprimerBtn.setOnAction(e -> {
                for (int i = 0; i < quantite; i++) {
                    panierDAO.remove(new Panier(client.getIdUser(), idArticle));
                }
                PagePrincipaleView.compteurPanier -= quantite;
                PagePrincipaleView.compteurPanierLabel.setText("(" + PagePrincipaleView.compteurPanier + ")");
                afficherPanier();
            });

            VBox boutons = new VBox(5, plusBtn, moinsBtn, supprimerBtn);
            boutons.setAlignment(Pos.CENTER);

            HBox ligne = new HBox(20, info, boutons);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setPadding(new Insets(10));
            ligne.setStyle("-fx-border-color: lightgray; -fx-background-radius: 10; -fx-border-radius: 10;");
            HBox.setHgrow(info, Priority.ALWAYS);

            panierBox.getChildren().add(ligne);
        }

        totalLabel.setText("Total : " + String.format("%.2f", totalGeneral) + " €");
    }
}
