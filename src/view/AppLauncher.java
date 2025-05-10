package view;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX.
 * Permet de lancer l'application et d'afficher l'écran d'accueil.
 */
public class AppLauncher extends Application {

    private static Stage primaryStage;

    /**
     * Méthode appelée automatiquement au démarrage de l'application.
     *
     * @param stage la fenêtre principale
     * @throws Exception si une erreur survient lors du chargement de l'accueil
     */
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showAccueil();
    }

    /**
     * Affiche la vue d'accueil de l'application.
     *
     * @throws Exception si la vue échoue à se charger
     */
    public static void showAccueil() throws Exception {
        AccueilView accueil = new AccueilView();
        accueil.start(primaryStage);
    }

    /**
     * Permet d'accéder à la fenêtre principale de l'application.
     *
     * @return la fenêtre principale (Stage)
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Point d'entrée principal de l'application JavaFX.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args); // Démarrage de l'application JavaFX
    }
}
