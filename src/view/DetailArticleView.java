package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Article;

import java.io.ByteArrayInputStream;

/**
 * Vue permettant d'afficher les détails d'un article pour l'administrateur.
 */
public class DetailArticleView {

    private final Article article;

    /**
     * Constructeur prenant l'article à afficher.
     *
     * @param article l'article sélectionné
     */
    public DetailArticleView(Article article) {
        this.article = article;
    }

    /**
     * Lance l'affichage de la vue de détail de l'article.
     *
     * @param stage la fenêtre principale
     */
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER_LEFT);

        grid.add(new Text("Nom : "), 0, 0);
        grid.add(new Text(article.getNomArticle()), 1, 0);

        grid.add(new Text("Prix à l'unité : "), 0, 1);
        grid.add(new Text(article.getPrixUnite() + " €"), 1, 1);

        grid.add(new Text("Prix en vrac : "), 0, 2);
        grid.add(new Text(article.getPrixVrac() + " €"), 1, 2);

        grid.add(new Text("Réduction dès : "), 0, 3);
        grid.add(new Text(article.getModuloReduction() + " unités"), 1, 3);

        grid.add(new Text("Marque : "), 0, 4);
        grid.add(new Text(article.getMarque()), 1, 4);

        grid.add(new Text("Quantité dispo : "), 0, 5);
        grid.add(new Text(String.valueOf(article.getQuantiteDispo())), 1, 5);

        grid.add(new Text("Note : "), 0, 6);
        grid.add(new Text(String.valueOf(article.getNote())), 1, 6);

        grid.add(new Text("Description : "), 0, 7);
        grid.add(new Text(article.getDescription()), 1, 7);

        if (article.getImageBytes() != null) {
            try {
                Image image = new Image(new ByteArrayInputStream(article.getImageBytes()));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);
                grid.add(new Text("Image : "), 0, 8);
                grid.add(imageView, 1, 8);
            } catch (Exception e) {
                System.err.println("Erreur d'affichage d'image : " + e.getMessage());
            }
        }

        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> new ArticleManagementView().start(stage));
        grid.add(btnRetour, 1, 9);

        Scene scene = new Scene(grid, 500, 600);
        stage.setTitle("Détail de l'article");
        stage.setScene(scene);
        stage.show();
    }
}
