package view;

import dao.CompteDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Compte;

import java.util.ArrayList;
import java.util.List;

public class GestionComptesView {

    public void start(Stage stage) {
        Text titre = new Text("👥 Gestion des comptes");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher un client par login ou ID...");
        HBox searchBox = new HBox(10, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        VBox listeComptesBox = new VBox(10);
        listeComptesBox.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(listeComptesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(600);

        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> new AdminView().start(stage));
        HBox retourBox = new HBox(btnRetour);
        retourBox.setAlignment(Pos.BOTTOM_RIGHT);
        retourBox.setPadding(new Insets(0, 20, 10, 0));

        VBox root = new VBox(20, titre, searchBox, scrollPane, retourBox);
        root.setPadding(new Insets(20));

        List<Compte> allComptes = new CompteDAO().findAll();
        afficherComptes(listeComptesBox, allComptes, stage);

        // 🔎 Recherche dynamique
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String search = newVal.trim().toLowerCase();
            List<Compte> filtered = new ArrayList<>();
            for (Compte c : allComptes) {
                if (c.getLogin().toLowerCase().contains(search) || String.valueOf(c.getIdUser()).equals(search)) {
                    filtered.add(c);
                }
            }
            afficherComptes(listeComptesBox, filtered, stage);
        });

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Gestion des comptes");
        stage.show();
    }

    private void afficherComptes(VBox container, List<Compte> comptes, Stage stage) {
        container.getChildren().clear();

        for (Compte compte : comptes) {
            HBox ligne = new HBox(10);
            ligne.setPadding(new Insets(10));
            ligne.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5px;");

            Text loginText = new Text(compte.getLogin());
            loginText.setStyle("-fx-font-size: 15px;");

            Label statut = new Label(compte.isAdmin() ? "🛡️ Admin" : "👤 Non admin");

            Button btnChangerRole = new Button(compte.isAdmin() ? "⇩ Rendre non admin" : "⇧ Rendre admin");
            Button btnDetail = new Button("Détail");
            Button btnSupprimer = new Button("Supprimer");

            // Action : changer rôle admin
            btnChangerRole.setOnAction(e -> {
                boolean nouveauStatut = !compte.isAdmin();
                boolean updated = new CompteDAO().updateAdminStatus(compte.getIdUser(), nouveauStatut);
                if (updated) {
                    new GestionComptesView().start(stage); // refresh
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText("Impossible de changer le statut admin.");
                    error.showAndWait();
                }
            });

            btnDetail.setOnAction(e -> {
                new DetailCompteView(compte).start(stage);
            });

            // Action : suppression
            btnSupprimer.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText("Voulez-vous vraiment supprimer ce compte ?");
                confirm.setContentText(compte.getLogin());

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean deleted = new CompteDAO().delete(compte.getIdUser());
                        if (deleted) {
                            new GestionComptesView().start(stage); // refresh
                        } else {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Erreur");
                            error.setHeaderText("La suppression a échoué.");
                            error.showAndWait();
                        }
                    }
                });
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            ligne.getChildren().addAll(loginText, statut, spacer, btnChangerRole, btnDetail, btnSupprimer);
            container.getChildren().add(ligne);
        }
    }
}
