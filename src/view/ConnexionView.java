package view;

import dao.CompteDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Compte;

public class ConnexionView {

    public void start(Stage stage) {
        // Champs
        TextField loginField = new TextField();
        PasswordField mdpField = new PasswordField();

        Button btnConnexion = new Button("Connexion");
        Button btnRetour = new Button("Retour");

        Label feedback = new Label();

        // Layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Login :"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Mot de passe :"), 0, 1);
        grid.add(mdpField, 1, 1);
        grid.add(btnConnexion, 0, 2);
        grid.add(btnRetour, 1, 2);
        grid.add(feedback, 0, 3, 2, 1);

        // Bouton retour
        btnRetour.setOnAction(e -> {
            try {
                AppLauncher.showAccueil();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Bouton connexion
        btnConnexion.setOnAction(e -> {
            String login = loginField.getText();
            String mdp = mdpField.getText();

            if (login.isBlank() || mdp.isBlank()) {
                feedback.setText("❌ Veuillez remplir tous les champs.");
                return;
            }

            CompteDAO dao = new CompteDAO();
            Compte compte = dao.findByLogin(login);

            if (compte != null && compte.getMdp().equals(mdp)) {
                feedback.setText("✅ Connexion réussie !");
                // TODO : Aller vers la page d’accueil utilisateur
            } else {
                feedback.setText("⚠️ Compte introuvable. Redirection vers l'inscription...");
                // Rediriger vers inscription après une petite pause
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> {
                            try {
                                new InscriptionView().start(AppLauncher.getPrimaryStage());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    } catch (InterruptedException ignored) {
                    }
                }).start();
            }
        });

        stage.setTitle("Connexion");
        stage.setScene(new Scene(grid, 400, 250));
        stage.show();
    }
}
