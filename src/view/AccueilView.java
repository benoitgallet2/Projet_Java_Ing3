package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Vue d'accueil de l'application.
 * Propose les options de connexion ou d'inscription.
 */
public class AccueilView {

    /**
     * Affiche la page d'accueil avec les boutons de navigation.
     *
     * @param stage la fenêtre principale de l'application
     */
    public void start(Stage stage) {
        Text titre = new Text("Bienvenue dans l'application Shopping");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnConnexion = new Button("Connexion");
        Button btnInscription = new Button("Inscription");

        btnConnexion.setOnAction(e -> {
            try {
                new ConnexionView().start(AppLauncher.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnInscription.setOnAction(e -> {
            try {
                new InscriptionView().start(AppLauncher.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(20, titre, btnConnexion, btnInscription);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30px;");

        Scene scene = new Scene(layout, 400, 250);
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.show();
    }
}
