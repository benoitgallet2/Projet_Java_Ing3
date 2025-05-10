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

/**
 * Vue du panier client, permettant de consulter, modifier ou valider la commande.
 */
public class PanierView {

    private final Client client;
    private final VBox panierBox = new VBox(10);
    private final Label totalLabel = new Label("Total : 0.00 €");

    /**
     * Constructeur principal du panier.
     *
     * @param client le client connecté
     */
    public PanierView(Client client) {
        this.client = client;
    }

    /**
     * Lance l'affichage de la vue du panier.
     *
     * @param stage fenêtre principale
     */
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
            double total = getTotalPanier();
            if (total > 0) {
                new ValidationCommandeView(client, total).start(stage);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Votre panier est vide !");
                alert.showAndWait();
            }
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

    /**
     * Affiche dynamiquement les articles présents dans le panier avec leur quantité,
     * leurs boutons de modification, et met à jour le total.
     */
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

            double prixSansReduction = quantite * article.getPrixUnite();
            double totalLigne;
            boolean reductionActive = false;

            if (article.getPrixVrac() != null && article.getModuloReduction() > 0 && quantite >= article.getModuloReduction()) {
                totalLigne = quantite * article.getPrixVrac();
                reductionActive = true;
                Label promo = new Label("Prix vrac appliqué");
                promo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                info.getChildren().add(promo);
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
                if (quantite < article.getQuantiteDispo()) {
                    panierDAO.add(new Panier(client.getIdUser(), idArticle));
                    PagePrincipaleView.compteurPanier++;
                    PagePrincipaleView.compteurPanierLabel.setText("(" + PagePrincipaleView.compteurPanier + ")");
                    afficherPanier();
                } else {
                    showAlert("Stock insuffisant pour cet article !");
                }
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

        if (panierBox.getChildren().isEmpty()) {
            Label vide = new Label("Votre panier est vide");
            vide.setFont(Font.font(18));
            vide.setStyle("-fx-text-fill: gray;");
            panierBox.getChildren().add(vide);
        }
    }

    /**
     * Calcule le total actuel du panier en prenant en compte les réductions.
     *
     * @return le montant total en euros
     */
    private double getTotalPanier() {
        double total = 0;
        PanierDAO panierDAO = new PanierDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        Map<Integer, Integer> quantites = panierDAO.getQuantitesByUser(client.getIdUser());

        for (Map.Entry<Integer, Integer> entry : quantites.entrySet()) {
            int idArticle = entry.getKey();
            int quantite = entry.getValue();
            Article article = articleDAO.findById(idArticle);
            if (article == null) continue;

            if (article.getPrixVrac() != null && article.getModuloReduction() > 0 && quantite >= article.getModuloReduction()) {
                total += quantite * article.getPrixVrac();
            } else {
                total += quantite * article.getPrixUnite();
            }
        }

        return total;
    }

    /**
     * Affiche une alerte graphique à l'utilisateur.
     *
     * @param message texte de l'alerte
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Stock");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
