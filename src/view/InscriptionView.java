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
 * Vue JavaFX permettant à un nouvel utilisateur de s’inscrire.
 * Cette vue gère la création du compte et du client associé.
 */
public class InscriptionView {

    /**
     * Affiche l’interface d’inscription pour les nouveaux utilisateurs.
     *
     * @param stage la fenêtre principale de l’application
     */
    public void start(Stage stage) {
        TextField loginField = new TextField();
        PasswordField mdpField = new PasswordField();
        TextField nomField = new TextField();
        TextField prenomField = new TextField();

        Button validerBtn = new Button("S'inscrire");
        Button retourBtn = new Button("Retour");

        Label feedback = new Label();

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER);

        form.add(new Label("Login :"), 0, 0);
        form.add(loginField, 1, 0);
        form.add(new Label("Mot de passe :"), 0, 1);
        form.add(mdpField, 1, 1);
        form.add(new Label("Nom :"), 0, 2);
        form.add(nomField, 1, 2);
        form.add(new Label("Prénom :"), 0, 3);
        form.add(prenomField, 1, 3);
        form.add(validerBtn, 0, 4);
        form.add(retourBtn, 1, 4);
        form.add(feedback, 0, 5, 2, 1);

        retourBtn.setOnAction(e -> {
            try {
                AppLauncher.showAccueil();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        validerBtn.setOnAction(e -> {
            String login = loginField.getText();
            String mdp = mdpField.getText();
            String nom = nomField.getText();
            String prenom = prenomField.getText();

            if (login.isBlank() || mdp.isBlank() || nom.isBlank() || prenom.isBlank()) {
                feedback.setText("Tous les champs sont obligatoires.");
                return;
            }

            CompteDAO compteDAO = new CompteDAO();
            ClientDAO clientDAO = new ClientDAO();

            Compte existing = compteDAO.findByLogin(login);
            if (existing != null) {
                feedback.setText("Ce login est déjà utilisé.");
                return;
            }

            Compte compte = new Compte(login, mdp, false);
            int idUser = compteDAO.create(compte);

            if (idUser != -1) {
                Client client = new Client(new Compte(idUser, login, mdp, false), nom, prenom);
                boolean ok = clientDAO.create(client);

                if (ok) {
                    feedback.setText("Inscription réussie !");
                } else {
                    feedback.setText("Compte créé mais client non enregistré !");
                }
            } else {
                feedback.setText("Erreur lors de la création du compte.");
            }
        });

        Scene scene = new Scene(form, 400, 300);
        stage.setTitle("Inscription");
        stage.setScene(scene);
        stage.show();
    }
}
