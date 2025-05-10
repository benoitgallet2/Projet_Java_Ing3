package view;

import dao.ClientDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Client;

/**
 * Vue JavaFX permettant à un client de modifier ses informations personnelles.
 */
public class ModifierProfilView {

    private final Client client;

    /**
     * Constructeur de la vue.
     *
     * @param client le client connecté
     */
    public ModifierProfilView(Client client) {
        this.client = client;
    }

    /**
     * Affiche une fenêtre modale permettant de modifier le profil du client.
     *
     * @param parentStage la fenêtre principale (appelante)
     */
    public void show(Stage parentStage) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(parentStage);
        popup.setTitle("Modifier mes informations");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField nomField = new TextField(client.getNom());
        TextField prenomField = new TextField(client.getPrenom());
        PasswordField mdpField = new PasswordField();
        PasswordField mdpConfirmField = new PasswordField();

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Label successLabel = new Label();
        successLabel.setTextFill(Color.GREEN);
        successLabel.setVisible(false);

        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);

        grid.add(new Label("Prénom :"), 0, 1);
        grid.add(prenomField, 1, 1);

        grid.add(new Label("Nouveau mot de passe :"), 0, 2);
        grid.add(mdpField, 1, 2);

        grid.add(new Label("Confirmer mot de passe :"), 0, 3);
        grid.add(mdpConfirmField, 1, 3);

        grid.add(errorLabel, 0, 4, 2, 1);
        grid.add(successLabel, 0, 5, 2, 1);

        Button btnValider = new Button("Valider");
        btnValider.setOnAction(e -> {
            errorLabel.setText("");
            mdpField.setStyle(null);
            mdpConfirmField.setStyle(null);
            successLabel.setVisible(false);

            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String mdp = mdpField.getText();
            String mdpConfirm = mdpConfirmField.getText();

            if (!mdp.isEmpty() && !mdp.equals(mdpConfirm)) {
                errorLabel.setText("Les mots de passe sont différents.");
                mdpField.setStyle("-fx-border-color: red;");
                mdpConfirmField.setStyle("-fx-border-color: red;");
                return;
            }

            client.setNom(nom);
            client.setPrenom(prenom);
            if (!mdp.isEmpty()) {
                client.setMdp(mdp);
            }

            new ClientDAO().update(client);

            successLabel.setText("Modifié avec succès");
            successLabel.setVisible(true);
        });

        Button btnAnnuler = new Button("Annuler");
        btnAnnuler.setOnAction(e -> popup.close());

        grid.add(btnValider, 0, 6);
        grid.add(btnAnnuler, 1, 6);

        Scene scene = new Scene(grid, 400, 320);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
