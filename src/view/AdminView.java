package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AdminView {

    public void start(Stage stage) {
        Text titre = new Text("🎛️ Panneau d'administration");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnArticles = new Button("Gestion des articles");
        Button btnClients = new Button("Gestion des clients");
        Button btnHistoriqueAchats = new Button("Historique des achats");
        Button btnRetour = new Button("Déconnexion");

        // Actions (gestion des articles seulement pour l’instant)
        btnArticles.setOnAction(e -> {
            try {
                new ArticleManagementView().start(AppLauncher.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        btnClients.setOnAction(e -> {
            try {
                new GestionComptesView().start(stage); // 🚀 ouvre la vue de gestion des comptes
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnHistoriqueAchats.setOnAction(e -> {
            try{
                new HistoriqueAchatsView().start(stage);
            }
            catch (Exception ex){
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

        VBox layout = new VBox(20, titre, btnArticles, btnClients, btnHistoriqueAchats, btnRetour);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 400, 300);
        stage.setTitle("Admin - Accueil");
        stage.setScene(scene);
        stage.show();
    }
}
