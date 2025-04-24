package view;

import dao.ArticleDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Article;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ModifierArticleView {

    private final Article article;

    public ModifierArticleView(Article article) {
        this.article = article;
    }

    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Champs pré-remplis
        TextField nomField = new TextField(article.getNomArticle());
        TextField prixUniteField = new TextField(String.valueOf(article.getPrixUnite()));
        TextField prixVracField = new TextField(String.valueOf(article.getPrixVrac()));
        TextField moduloField = new TextField(String.valueOf(article.getModuloReduction()));
        TextField marqueField = new TextField(article.getMarque());
        TextField quantiteField = new TextField(String.valueOf(article.getQuantiteDispo()));
        TextField noteField = new TextField(String.valueOf(article.getNote()));
        TextArea descField = new TextArea(article.getDescription());

        // Image
        Button choisirImage = new Button("Choisir une image");
        Label imageLabel = new Label("Image actuelle conservée");

        final InputStream[] imageStream = { null }; // Par défaut = pas de nouvelle image

        choisirImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une nouvelle image");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    imageStream[0] = new FileInputStream(file);
                    imageLabel.setText("Nouvelle image : " + file.getName());
                } catch (Exception ex) {
                    imageLabel.setText("❌ Erreur d’image");
                    ex.printStackTrace();
                }
            }
        });

        // Feedback et boutons
        Label feedback = new Label();
        Button enregistrer = new Button("Enregistrer");
        Button annuler = new Button("Annuler");

        enregistrer.setOnAction(e -> {
            try {
                article.setNomArticle(nomField.getText());
                article.setPrixUnite(Double.parseDouble(prixUniteField.getText()));
                article.setPrixVrac(Double.parseDouble(prixVracField.getText()));
                article.setModuloReduction(Integer.parseInt(moduloField.getText()));
                article.setMarque(marqueField.getText());
                article.setQuantiteDispo(Double.parseDouble(quantiteField.getText()));
                article.setNote(Integer.parseInt(noteField.getText()));
                article.setDescription(descField.getText());

                // 🔄 Gestion intelligente de l'image
                if (imageStream[0] != null) {
                    article.setImageStream(imageStream[0]); // Nouvelle image sélectionnée
                } else if (article.getImageBytes() != null) {
                    article.setImageStream(new ByteArrayInputStream(article.getImageBytes())); // Réutilisation de l'existante
                }

                boolean updated = new ArticleDAO().update(article);

                if (updated) {
                    feedback.setText("✅ Article modifié !");
                    new ArticleManagementView().start(stage);
                } else {
                    feedback.setText("❌ Échec de la modification.");
                }
            } catch (Exception ex) {
                feedback.setText("⚠️ Erreur : " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        annuler.setOnAction(e -> new ArticleManagementView().start(stage));

        // Ajout des éléments à la grille
        grid.addRow(0, new Label("Nom : "), nomField);
        grid.addRow(1, new Label("Prix à l'unité : "), prixUniteField);
        grid.addRow(2, new Label("Prix en vrac : "), prixVracField);
        grid.addRow(3, new Label("Modulo réduction : "), moduloField);
        grid.addRow(4, new Label("Marque : "), marqueField);
        grid.addRow(5, new Label("Quantité dispo : "), quantiteField);
        grid.addRow(6, new Label("Note : "), noteField);
        grid.addRow(7, new Label("Description : "), descField);
        grid.addRow(8, choisirImage, imageLabel);
        grid.addRow(9, feedback);

        HBox buttons = new HBox(10, enregistrer, annuler);
        grid.add(buttons, 1, 10);

        // Scène
        Scene scene = new Scene(grid, 500, 600);
        stage.setTitle("Modifier un article");
        stage.setScene(scene);
        stage.show();
    }
}
