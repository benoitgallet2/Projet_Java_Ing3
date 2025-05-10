package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Client;

/**
 * Vue de confirmation de commande pour le client.
 * Affiche un message de succès après validation de la commande
 * et propose un retour à l'accueil.
 */
public class CommandeConfirmeeView {

    private final Client client;

    /**
     * Constructeur avec client connecté.
     *
     * @param client le client qui a passé commande
     */
    public CommandeConfirmeeView(Client client) {
        this.client = client;
    }

    /**
     * Démarre la vue de confirmation de commande.
     *
     * @param stage la fenêtre principale
     */
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label message = new Label("Votre commande a été validée avec succès !");
        message.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button retourAccueil = new Button("Retour à l'accueil");
        retourAccueil.setOnAction(e -> new PagePrincipaleView(client).start(stage));

        root.getChildren().addAll(message, retourAccueil);

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.setTitle("Commande confirmée");
        stage.show();
    }
}
