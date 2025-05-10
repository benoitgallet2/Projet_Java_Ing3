package view;

import dao.ArticleDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Article;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Vue pour l'ajout d'un nouvel article dans l'application.
 * Permet à l'administrateur de saisir les informations d'un article et de l'enregistrer.
 */
public class AjoutArticleView {

    private File imageFile = null;

    /**
     * Affiche l'interface d'ajout d'un article.
     *
     * @param stage la fenêtre principale sur laquelle afficher la scène
     */
    public void start(Stage stage) {
        TextField nomField = new TextField();
        TextField prixUniteField = new TextField();
        TextField prixVracField = new TextField();
        TextField moduloField = new TextField();
        TextField marqueField = new TextField();
        TextField quantiteField = new TextField();
        TextArea descriptionField = new TextArea();

        Button btnImage = new Button("Choisir une image");
        ImageView imagePreview = new ImageView();
        imagePreview.setFitHeight(100);
        imagePreview.setPreserveRatio(true);

        btnImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );
            File selected = fileChooser.showOpenDialog(stage);
            if (selected != null) {
                imageFile = selected;
                imagePreview.setImage(new Image(selected.toURI().toString()));
            }
        });

        Button btnValider = new Button("Ajouter");
        Button btnAnnuler = new Button("Retour");
        Label feedback = new Label();

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Nom de l'article:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prix à l'unité:"), 0, 1);
        grid.add(prixUniteField, 1, 1);
        grid.add(new Label("Prix en vrac:"), 0, 2);
        grid.add(prixVracField, 1, 2);
        grid.add(new Label("Modulo réduction:"), 0, 3);
        grid.add(moduloField, 1, 3);
        grid.add(new Label("Marque:"), 0, 4);
        grid.add(marqueField, 1, 4);
        grid.add(new Label("Quantité disponible:"), 0, 5);
        grid.add(quantiteField, 1, 5);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionField, 1, 6);
        grid.add(btnImage, 0, 7);
        grid.add(imagePreview, 1, 7);
        grid.add(btnValider, 0, 8);
        grid.add(btnAnnuler, 1, 8);
        grid.add(feedback, 0, 9, 2, 1);

        btnValider.setOnAction(e -> {
            try {
                InputStream imageStream = null;
                if (imageFile != null) {
                    imageStream = new FileInputStream(imageFile);
                }

                Article article = new Article(
                        nomField.getText(),
                        Double.parseDouble(prixUniteField.getText()),
                        Double.parseDouble(prixVracField.getText()),
                        Integer.parseInt(moduloField.getText()),
                        marqueField.getText(),
                        Double.parseDouble(quantiteField.getText()),
                        imageStream,
                        0,
                        descriptionField.getText()
                );

                boolean ok = new ArticleDAO().create(article);
                if (ok) {
                    feedback.setText("Article ajouté avec succès !");
                } else {
                    feedback.setText("Échec de l'ajout.");
                }
            } catch (Exception ex) {
                feedback.setText("Erreur : " + ex.getMessage());
            }
        });

        btnAnnuler.setOnAction(e -> {
            try {
                new ArticleManagementView().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(grid, 600, 600);
        stage.setTitle("Ajout d’un article");
        stage.setScene(scene);
        stage.show();
    }
}
