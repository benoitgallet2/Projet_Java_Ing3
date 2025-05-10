package view;

import dao.ArticleDAO;
import dao.CommandeDAO;
import dao.LigneCommandeDAO;
import dao.PanierDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Article;
import model.Client;
import model.Commande;
import model.Panier;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Vue JavaFX pour valider une commande.
 * Gère la saisie des informations de livraison, le paiement, et enregistre la commande.
 */
public class ValidationCommandeView {

    private final Client client;
    private final double totalCommande;

    /**
     * Constructeur de la vue.
     *
     * @param client          Le client connecté.
     * @param totalCommande   Le montant total de la commande.
     */
    public ValidationCommandeView(Client client, double totalCommande) {
        this.client = client;
        this.totalCommande = totalCommande;
    }

    /**
     * Démarre l'affichage de la scène JavaFX.
     *
     * @param stage La fenêtre principale.
     */
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Label titre = new Label("Validation de votre commande");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label recap = new Label("Montant total : " + String.format("%.2f", totalCommande) + " €");

        TextField adresseField = new TextField();
        adresseField.setPromptText("Adresse de livraison");

        TextField cpField = new TextField();
        cpField.setPromptText("Code postal");

        TextField villeField = new TextField();
        villeField.setPromptText("Ville");

        VBox livraisonBox = new VBox(10, adresseField, cpField, villeField);

        ComboBox<String> paiementBox = new ComboBox<>();
        paiementBox.getItems().addAll("Carte bancaire", "Paypal", "Paiement à la livraison");
        paiementBox.setValue("Carte bancaire");

        VBox paiementForm = new VBox(10);
        paiementForm.setPadding(new Insets(10));

        TextField cbNumero = new TextField();
        cbNumero.setPromptText("Numéro de carte");

        DatePicker cbDate = new DatePicker();
        cbDate.setPromptText("Date d’expiration");
        cbDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        TextField cbCVV = new TextField();
        cbCVV.setPromptText("Cryptogramme (CVV)");

        TextField paypalEmail = new TextField();
        paypalEmail.setPromptText("Adresse email PayPal");

        Label infoVirement = new Label("Merci de faire un virement à :\nIBAN : FR76 3000 4000 5000 6000 7000 000");

        paiementBox.setOnAction(e -> {
            paiementForm.getChildren().clear();
            switch (paiementBox.getValue()) {
                case "Carte bancaire" -> paiementForm.getChildren().addAll(cbNumero, cbDate, cbCVV);
                case "Paypal" -> paiementForm.getChildren().add(paypalEmail);
                case "Paiement à la livraison" -> paiementForm.getChildren().add(infoVirement);
            }
        });
        paiementBox.getOnAction().handle(null);

        Button validerBtn = new Button("Payer et valider la commande");
        Label feedback = new Label();

        validerBtn.setOnAction(e -> {
            String adresse = adresseField.getText();
            String ville = villeField.getText();
            String codePostal = cpField.getText();
            String numCarte = cbNumero.getText();
            String cvv = cbCVV.getText();

            if (adresse.isBlank() || ville.isBlank() || codePostal.isBlank()) {
                feedback.setText("Merci de remplir toutes les informations de livraison !");
                return;
            }

            if (ville.matches(".*\\d.*")) {
                feedback.setText("La ville ne doit pas contenir de chiffres.");
                return;
            }

            if (!codePostal.matches("\\d+")) {
                feedback.setText("Le code postal doit contenir uniquement des chiffres.");
                return;
            }

            if (paiementBox.getValue().equals("Carte bancaire")) {
                if (!numCarte.matches("\\d+")) {
                    feedback.setText("Le numéro de carte doit contenir uniquement des chiffres.");
                    return;
                }
                if (cbDate.getValue() == null || cbDate.getValue().isBefore(LocalDate.now())) {
                    feedback.setText("Veuillez sélectionner une date de validité correcte.");
                    return;
                }
                if (!cvv.matches("\\d{3,4}")) {
                    feedback.setText("Le cryptogramme est invalide.");
                    return;
                }
            }

            CommandeDAO commandeDAO = new CommandeDAO();
            Commande commande = new Commande(
                    totalCommande,
                    Date.valueOf(LocalDate.now()),
                    client.getIdUser(),
                    null,
                    "Payé",
                    adresse,
                    ville,
                    codePostal
            );

            int idCommande = commandeDAO.create(commande);

            if (idCommande > 0) {
                LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
                PanierDAO panierDAO = new PanierDAO();
                ArticleDAO articleDAO = new ArticleDAO();
                List<Panier> panier = panierDAO.getPanierByUser(client.getIdUser());

                for (Panier p : panier) {
                    ligneDAO.addArticleToCommande(idCommande, p.getIdArticle());

                    Article article = articleDAO.findById(p.getIdArticle());
                    if (article != null) {
                        double qte = article.getQuantiteDispo();
                        if (qte > 0) {
                            article.setQuantiteDispo(qte - 1);
                            articleDAO.update(article);
                        }
                    }
                }

                panierDAO.clearUserPanier(client.getIdUser());
                PagePrincipaleView.compteurPanier = 0;
                PagePrincipaleView.compteurPanierLabel.setText("(0)");

                new CommandeConfirmeeView(client).start(stage);
            } else {
                feedback.setText("Erreur lors de la création de la commande.");
            }
        });

        root.getChildren().addAll(
                titre, recap,
                new Label("Adresse de livraison :"), livraisonBox,
                new Label("Mode de paiement :"), paiementBox, paiementForm,
                validerBtn, feedback
        );

        Scene scene = new Scene(root, 600, 550);
        stage.setScene(scene);
        stage.setTitle("Validation commande");
        stage.show();
    }
}
