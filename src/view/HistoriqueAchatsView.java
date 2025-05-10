package view;

import dao.CommandeDAO;
import dao.CompteDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Commande;
import model.Compte;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vue JavaFX permettant à l'administrateur de consulter l'historique
 * des achats passés par les utilisateurs.
 */
public class HistoriqueAchatsView {

    private List<Commande> commandes;
    private final CompteDAO compteDAO = new CompteDAO();
    private GridPane grid;
    private TextField searchLogin;
    private ComboBox<String> filterStatut;
    private ComboBox<String> triCombo;

    /**
     * Affiche la vue principale d’historique des commandes.
     *
     * @param stage la fenêtre principale
     */
    public void start(Stage stage) {
        CommandeDAO commandeDAO = new CommandeDAO();
        commandes = commandeDAO.findAll();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        searchLogin = new TextField();
        searchLogin.setPromptText("Rechercher un login client...");
        searchLogin.setMinWidth(200);

        filterStatut = new ComboBox<>();
        filterStatut.getItems().addAll("Tous", "Payé", "Livré");
        filterStatut.setValue("Tous");

        triCombo = new ComboBox<>();
        triCombo.getItems().addAll("Tri par défaut", "Date ⬇️", "Date ⬆️", "Montant ⬇️", "Montant ⬆️");
        triCombo.setValue("Tri par défaut");

        HBox filters = new HBox(15, new Text("Filtrer par :"), searchLogin, filterStatut, triCombo);
        filters.setAlignment(Pos.CENTER_LEFT);

        grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);

        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> new AdminView().start(stage));
        HBox bottom = new HBox(btnRetour);
        bottom.setAlignment(Pos.BOTTOM_RIGHT);

        root.getChildren().addAll(new Text("🧾 Historique des commandes"), filters, scrollPane, bottom);

        searchLogin.textProperty().addListener((obs, oldVal, newVal) -> updateGrid());
        filterStatut.setOnAction(e -> updateGrid());
        triCombo.setOnAction(e -> updateGrid());

        updateGrid();

        Scene scene = new Scene(root, 850, 550);
        stage.setScene(scene);
        stage.setTitle("Historique des achats");
        stage.show();
    }

    /**
     * Met à jour l'affichage du tableau des commandes en fonction des filtres et tris sélectionnés.
     */
    private void updateGrid() {
        grid.getChildren().clear();

        int row = 0;
        grid.add(new Text("ID"), 0, row);
        grid.add(new Text("Date"), 1, row);
        grid.add(new Text("Montant"), 2, row);
        grid.add(new Text("Client"), 3, row);
        grid.add(new Text("Statut"), 4, row);
        grid.add(new Text(""), 5, row++); // Colonne pour le bouton

        String search = searchLogin.getText().trim().toLowerCase();
        String statut = filterStatut.getValue();
        String tri = triCombo.getValue();

        List<Commande> filtered = commandes.stream()
                .filter(c -> {
                    Compte compte = compteDAO.findById(c.getIdUser());
                    boolean matchLogin = (compte != null) && compte.getLogin().toLowerCase().contains(search);
                    boolean matchStatut = statut.equals("Tous") || c.getStatut().equalsIgnoreCase(statut);
                    return matchLogin && matchStatut;
                })
                .collect(Collectors.toList());

        switch (tri) {
            case "Date ⬇️" -> filtered.sort(Comparator.comparing(Commande::getDate).reversed());
            case "Date ⬆️" -> filtered.sort(Comparator.comparing(Commande::getDate));
            case "Montant ⬇️" -> filtered.sort(Comparator.comparing(Commande::getMontant).reversed());
            case "Montant ⬆️" -> filtered.sort(Comparator.comparing(Commande::getMontant));
        }

        for (Commande c : filtered) {
            Compte compte = compteDAO.findById(c.getIdUser());

            grid.add(new Text("#" + c.getIdCommande()), 0, row);
            grid.add(new Text(c.getDate().toString()), 1, row);
            grid.add(new Text(c.getMontant() + " €"), 2, row);
            grid.add(new Text(compte != null ? compte.getLogin() : "Inconnu"), 3, row);
            grid.add(new Text(c.getStatut()), 4, row);

            Button btnDetail = new Button("Détail");
            btnDetail.setOnAction(e -> {
                Stage currentStage = (Stage) ((Button) e.getSource()).getScene().getWindow();
                new DetailCommandeView(c, compte, () -> start(currentStage)).start(currentStage);
            });

            grid.add(btnDetail, 5, row++);
        }

        if (filtered.isEmpty()) {
            grid.add(new Text("Aucune commande trouvée."), 0, row, 6, 1);
        }
    }
}
