package view;

import dao.ArticleDAO;
import dao.ClientDAO;
import dao.CommandeDAO;
import dao.PanierDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;
import model.Client;
import model.Commande;
import model.Compte;
import model.Panier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailCompteView {

    private final Compte compte;

    public DetailCompteView(Compte compte) {
        this.compte = compte;
    }

    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.TOP_LEFT);

        int row = 0;

        // 🧾 Infos Compte
        grid.add(new Text("ID Utilisateur : "), 0, row);
        grid.add(new Text(String.valueOf(compte.getIdUser())), 1, row++);

        grid.add(new Text("Login : "), 0, row);
        grid.add(new Text(compte.getLogin()), 1, row++);

        grid.add(new Text("Mot de passe : "), 0, row);
        grid.add(new Text("********"), 1, row++); // masqué volontairement

        grid.add(new Text("Statut : "), 0, row);
        grid.add(new Text(compte.isAdmin() ? "🛡️ Admin" : "👤 Non admin"), 1, row++);

        // 📄 Infos Client
        ClientDAO clientDAO = new ClientDAO();
        Client client = clientDAO.findClientByIdUser(compte.getIdUser());

        if (client != null) {
            grid.add(new Text("Nom : "), 0, row);
            grid.add(new Text(client.getNom()), 1, row++);

            grid.add(new Text("Prénom : "), 0, row);
            grid.add(new Text(client.getPrenom()), 1, row++);
        } else {
            grid.add(new Text("Ce compte n’est pas associé à un client."), 0, row++, 2, 1);
        }

        //📦 Commandes passées
        CommandeDAO commandeDAO = new CommandeDAO();
        List<Commande> commandes = commandeDAO.findByUser(compte.getIdUser());

        if (!commandes.isEmpty()) {
            grid.add(new Text("Commandes passées :"), 0, row++);
            for (Commande cmd : commandes) {
                Button btnCmd = new Button("Commande #" + cmd.getIdCommande() + " | " + cmd.getDate() + " | " + cmd.getStatut());
                btnCmd.setOnAction(e -> {
                    new DetailCommandeView(cmd, compte, () -> new DetailCompteView(compte).start(stage)).start(stage);
                });
                grid.add(btnCmd, 1, row++);
            }
        } else {
            grid.add(new Text("Aucune commande enregistrée."), 0, row++, 2, 1);
        }

        // 🛒 Panier actuel regroupé
        PanierDAO panierDAO = new PanierDAO();
        ArticleDAO articleDAO = new ArticleDAO();
        List<Panier> panierList = panierDAO.getPanierByUser(compte.getIdUser());

        Map<Integer, Integer> quantitesPanier = new HashMap<>();
        for (Panier p : panierList) {
            int articleId = p.getIdArticle();
            quantitesPanier.put(articleId, quantitesPanier.getOrDefault(articleId, 0) + 1);
        }

        if (!quantitesPanier.isEmpty()) {
            grid.add(new Text("Panier actuel :"), 0, row++);
            for (int articleId : quantitesPanier.keySet()) {
                Article a = articleDAO.findById(articleId);
                String nom = (a != null) ? a.getNomArticle() : "Article introuvable";
                int qte = quantitesPanier.get(articleId);
                grid.add(new Text("- " + nom + " (ID: " + articleId + ") : " + qte), 1, row++);
            }
        } else {
            grid.add(new Text("Panier vide."), 0, row++, 2, 1);
        }

        // 🔙 Retour
        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> new GestionComptesView().start(stage));
        grid.add(btnRetour, 1, row);

        stage.setScene(new Scene(grid, 600, 600));
        stage.setTitle("Détail du compte");
        stage.setScene(stage.getScene());
        stage.show();
    }
}
