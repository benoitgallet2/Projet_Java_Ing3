package view;

import dao.ArticleDAO;
import dao.CommandeDAO;
import dao.LigneCommandeDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Article;
import model.Commande;
import model.LigneCommande;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe permettant d'afficher une fenêtre de notifications pour l'administrateur.
 * Les alertes concernent :
 * - les articles avec un stock faible,
 * - les articles jamais commandés,
 * - les commandes non finalisées (statut autre que "Payé" ou "Livré").
 */
public class NotificationView {

    /**
     * Affiche une fenêtre modale contenant les alertes en cours.
     *
     * @param parentStage la fenêtre principale (admin)
     */
    public void showAlertWindow(Stage parentStage) {
        Stage popup = new Stage();
        popup.initOwner(parentStage);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("🔔 Alertes administrateur");

        VBox alertBox = new VBox(15);
        alertBox.setPadding(new Insets(20));

        ArticleDAO articleDAO = new ArticleDAO();
        List<Article> articles = articleDAO.findAll();

        // Stock faible
        for (Article a : articles) {
            if (a.getQuantiteDispo() < 5) {
                alertBox.getChildren().add(new Label("Stock faible : " + a.getNomArticle() + " (dispo : " + a.getQuantiteDispo() + ")"));
            }
        }

        // Articles jamais commandés
        LigneCommandeDAO ligneDAO = new LigneCommandeDAO();
        Set<Integer> articlesCommandes = new HashSet<>();
        for (LigneCommande l : ligneDAO.findAll()) {
            articlesCommandes.add(l.getIdArticle());
        }

        for (Article a : articles) {
            if (!articlesCommandes.contains(a.getIdArticle())) {
                alertBox.getChildren().add(new Label("Jamais commandé : " + a.getNomArticle()));
            }
        }

        // Commandes non finalisées
        CommandeDAO commandeDAO = new CommandeDAO();
        List<Commande> commandes = commandeDAO.findAll();
        for (Commande c : commandes) {
            String statut = c.getStatut().toLowerCase();
            if (!statut.equals("payé") && !statut.equals("livré")) {
                alertBox.getChildren().add(new Label("Commande #" + c.getIdCommande() + " (" + c.getStatut() + ")"));
            }
        }

        // Aucun problème
        if (alertBox.getChildren().isEmpty()) {
            alertBox.getChildren().add(new Label("Aucune alerte. Tout est sous contrôle !"));
        }

        ScrollPane scrollPane = new ScrollPane(alertBox);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 450, 400);
        popup.setScene(scene);
        popup.show();
    }
}
