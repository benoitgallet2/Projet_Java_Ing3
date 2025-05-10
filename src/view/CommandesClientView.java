package view;

import dao.ArticleDAO;
import dao.CommandeDAO;
import dao.LigneCommandeDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;
import model.Client;
import model.Commande;
import model.LigneCommande;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vue permettant à un client de consulter ses commandes passées.
 */
public class CommandesClientView {

    private final Client client;

    /**
     * Constructeur de la vue avec le client connecté.
     *
     * @param client le client concerné
     */
    public CommandesClientView(Client client) {
        this.client = client;
    }

    /**
     * Affiche la liste des commandes du client.
     *
     * @param stage la fenêtre principale
     */
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        Text titre = new Text("📦 Mes commandes");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.getChildren().add(titre);

        VBox listeCommandes = new VBox(15);
        listeCommandes.setPadding(new Insets(10));

        CommandeDAO commandeDAO = new CommandeDAO();
        List<Commande> commandes = commandeDAO.findByUser(client.getIdUser());

        if (commandes.isEmpty()) {
            listeCommandes.getChildren().add(new Text("Aucune commande passée."));
        } else {
            for (Commande commande : commandes) {
                VBox box = new VBox(5);
                box.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px;");
                box.setPadding(new Insets(10));

                box.getChildren().add(new Text("Commande #" + commande.getIdCommande()));
                box.getChildren().add(new Text("Montant : " + String.format("%.2f", commande.getMontant()) + " €"));
                box.getChildren().add(new Text("Date : " + commande.getDate()));
                box.getChildren().add(new Text("Statut : " + commande.getStatut()));

                Button detailBtn = new Button("Voir le détail");
                detailBtn.setOnAction(e -> afficherDetailsClient(commande, stage));
                box.getChildren().add(detailBtn);

                listeCommandes.getChildren().add(box);
            }
        }

        ScrollPane scrollPane = new ScrollPane(listeCommandes);
        scrollPane.setFitToWidth(true);

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> new PagePrincipaleView(client).start(stage));
        HBox footer = new HBox(retourBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(scrollPane, footer);

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Mes commandes");
        stage.show();
    }

    /**
     * Affiche les détails d'une commande : articles, livraison et possibilité de noter.
     *
     * @param commande la commande sélectionnée
     * @param stage    la scène principale
     */
    private void afficherDetailsClient(Commande commande, Stage stage) {
        VBox detailBox = new VBox(15);
        detailBox.setPadding(new Insets(20));

        Text titre = new Text("Détail de la commande #" + commande.getIdCommande());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        detailBox.getChildren().add(titre);

        detailBox.getChildren().add(new Text("Date : " + commande.getDate()));
        detailBox.getChildren().add(new Text("Montant : " + String.format("%.2f", commande.getMontant()) + " €"));
        detailBox.getChildren().add(new Text("Statut : " + commande.getStatut()));
        detailBox.getChildren().add(new Text("Adresse : " + commande.getAdresseLivraison() + ", " + commande.getCodePostal() + " " + commande.getVilleLivraison()));

        LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        List<LigneCommande> lignes = ligneDAO.findByCommandeId(commande.getIdCommande());

        Map<Integer, Integer> quantites = new HashMap<>();
        for (LigneCommande ligne : lignes) {
            int id = ligne.getIdArticle();
            quantites.put(id, quantites.getOrDefault(id, 0) + 1);
        }

        detailBox.getChildren().add(new Text("Articles commandés :"));
        for (int idArticle : quantites.keySet()) {
            Article a = articleDAO.findById(idArticle);
            if (a != null) {
                String nom = a.getNomArticle();
                int qte = quantites.get(idArticle);

                HBox ligne = new HBox(10);
                ligne.setAlignment(Pos.CENTER_LEFT);

                Label label = new Label("- " + nom + " (x" + qte + ")");
                Button noterBtn = new Button("Noter");
                noterBtn.setOnAction(ev -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Noter l'article");
                    dialog.setHeaderText("Note de 1 à 5");
                    dialog.setContentText("Votre note :");

                    dialog.showAndWait().ifPresent(noteStr -> {
                        try {
                            int note = Integer.parseInt(noteStr);
                            if (note < 1 || note > 5) {
                                showAlert("Veuillez entrer une note entre 1 et 5.");
                                return;
                            }

                            int nbCommandes = ligneDAO.countDistinctCommandesByArticle(a.getIdArticle()) - 1;
                            double ancienneNote = a.getNote();
                            double nouvelleNote = ((ancienneNote * nbCommandes) + note) / (nbCommandes + 1);

                            a.setNote((int) Math.round(nouvelleNote));
                            articleDAO.update(a);
                            showAlert("Merci pour votre note !");
                        } catch (NumberFormatException ex) {
                            showAlert("Entrée invalide. Entrez un chiffre entre 1 et 5.");
                        }
                    });
                });

                ligne.getChildren().addAll(label, noterBtn);
                detailBox.getChildren().add(ligne);
            }
        }

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> start(stage));
        detailBox.getChildren().add(retourBtn);

        Scene scene = new Scene(detailBox, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Détail commande");
        stage.show();
    }

    /**
     * Affiche une boîte d'alerte avec un message personnalisé.
     *
     * @param message le texte à afficher
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
