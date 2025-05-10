package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Vue principale du panneau d'administration.
 * Permet d'accéder aux différentes sections de gestion de l'application.
 * Présente tous les boutons redirigeant vers les fonctionnalités administrateur
 */
public class AdminView {

    /**
     * Lance la vue du panneau d'administration.
     *
     * @param stage la fenêtre principale à afficher
     */
    public void start(Stage stage) {
        Button notifBtn = new Button("🔔");
        notifBtn.setOnAction(e -> new NotificationView().showAlertWindow(stage));
        notifBtn.setStyle("-fx-font-size: 14px;");

        HBox topBar = new HBox(notifBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10, 20, 0, 0));

        Text titre = new Text("🎛️ Panneau d'administration");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnArticles = new Button("Gestion des articles");
        Button btnClients = new Button("Gestion des clients");
        Button btnHistoriqueAchats = new Button("Historique des achats");
        Button btnStats = new Button("Statistiques");
        Button btnPromos = new Button("Gestion des promotions");
        Button btnStocks = new Button("Gestion des stocks");
        Button btnRetour = new Button("Déconnexion");

        btnArticles.setOnAction(e -> {
            try {
                new ArticleManagementView().start(AppLauncher.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnClients.setOnAction(e -> {
            try {
                new GestionComptesView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnHistoriqueAchats.setOnAction(e -> {
            try {
                new HistoriqueAchatsView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnStats.setOnAction(e -> {
            try {
                new StatistiquesView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnPromos.setOnAction(e -> {
            try {
                new GestionPromotionsView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnStocks.setOnAction(e -> {
            try {
                new GestionStocksView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnRetour.setOnAction(e -> {
            try {
                AppLauncher.showAccueil();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox menuButtons = new VBox(20, titre, btnArticles, btnClients, btnHistoriqueAchats, btnStats, btnPromos, btnStocks, btnRetour);
        menuButtons.setAlignment(Pos.CENTER);
        menuButtons.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(menuButtons);

        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("Admin - Accueil");
        stage.setScene(scene);
        stage.show();
    }
}
