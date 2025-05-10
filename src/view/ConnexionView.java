package view;

import dao.ClientDAO;
import dao.CompteDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Client;
import model.Compte;

/**
 * Vue de connexion permettant à un utilisateur (admin ou client) de se connecter.
 */
public class ConnexionView {

    /**
     * Lance l'interface de connexion.
     *
     * @param stage la fenêtre principale
     */
    public void start(Stage stage) {
        TextField loginField = new TextField();
        PasswordField mdpField = new PasswordField();

        Button btnConnexion = new Button("Connexion");
        Button btnRetour = new Button("Retour");
        Label feedback = new Label();

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

        btnRetour.setOnAction(e -> {
            try {
                AppLauncher.showAccueil();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnConnexion.setOnAction(e -> {
            String login = loginField.getText();
            String mdp = mdpField.getText();

            if (login.isBlank() || mdp.isBlank()) {
                feedback.setText("Veuillez remplir tous les champs.");
                return;
            }

            CompteDAO compteDAO = new CompteDAO();
            Compte compte = compteDAO.findByLogin(login);

            if (compte != null && compte.getMdp().equals(mdp)) {
                if (compte.isAdmin()) {
                    try {
                        new AdminView().start(AppLauncher.getPrimaryStage());
                        feedback.setText("Connexion administrateur !");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ClientDAO clientDAO = new ClientDAO();
                    Client client = clientDAO.findClientByIdUser(compte.getIdUser());
                    if (client != null) {
                        new PagePrincipaleView(client).start(AppLauncher.getPrimaryStage());
                        feedback.setText("Connexion réussie !");
                    } else {
                        feedback.setText("Ce compte n'est pas associé à un client.");
                    }
                }
            } else {
                feedback.setText("Compte introuvable. Redirection vers l'inscription...");
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
