package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AccueilView {

    public void start(Stage stage) {
        // Titre
        Text titre = new Text("Bienvenue dans l'application Shopping");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Boutons
        Button btnConnexion = new Button("Connexion");
        Button btnInscription = new Button("Inscription");

        // Gestion des clics
        btnConnexion.setOnAction(e -> {
            try {
                new ConnexionView().start(AppLauncher.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnInscription.setOnAction(e -> {
            try {
                InscriptionView inscription = new InscriptionView();
                inscription.start(AppLauncher.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        // Layout
        VBox layout = new VBox(20, titre, btnConnexion, btnInscription);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30px;");

        // Scène
        Scene scene = new Scene(layout, 400, 250);
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.show();
    }
}
