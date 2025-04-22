package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showAccueil();
    }

    public static void showAccueil() throws Exception {
        AccueilView accueil = new AccueilView();
        accueil.start(primaryStage);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // plus tard : showConnexion(), showInscription(), etc.

    public static void main(String[] args) {
        launch(args); // Lancer JavaFX
    }
}
