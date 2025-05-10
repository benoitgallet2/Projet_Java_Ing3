package view;

import dao.ArticleDAO;
import dao.CommandeDAO;
import dao.LigneCommandeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Article;
import model.Commande;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vue JavaFX affichant des statistiques avancées (ventes, commandes, produits)
 * sous forme de graphiques (ligne, barres, camembert), filtrables par date et marque.
 */
public class StatistiquesAvanceesView {

    private DatePicker dateDebutPicker;
    private DatePicker dateFinPicker;
    private ComboBox<String> marqueCombo;
    private VBox root;
    private LineChart<String, Number> lineChart;
    private BarChart<String, Number> barChart;
    private PieChart pieChart;

    /**
     * Démarre la vue des statistiques avancées avec les filtres et les graphiques.
     *
     * @param stage la fenêtre dans laquelle afficher l'interface
     */
    public void start(Stage stage) {
        root = new VBox(20);
        root.setPadding(new Insets(20));

        // Champs filtres
        dateDebutPicker = new DatePicker();
        dateFinPicker = new DatePicker();

        marqueCombo = new ComboBox<>();
        marqueCombo.getItems().add("Toutes marques");
        marqueCombo.getItems().addAll(new ArticleDAO().findAll().stream()
                .map(Article::getMarque)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList());
        marqueCombo.setValue("Toutes marques");

        HBox filtres = new HBox(15,
                new Label("Date de :"), dateDebutPicker,
                new Label("à"), dateFinPicker,
                new Label("Marque :"), marqueCombo
        );
        filtres.setPadding(new Insets(10));

        // Graphiques
        lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        pieChart = new PieChart();

        // Filtres dynamiques
        dateDebutPicker.setOnAction(e -> updateCharts());
        dateFinPicker.setOnAction(e -> updateCharts());
        marqueCombo.setOnAction(e -> updateCharts());

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> new StatistiquesView().start(stage));

        root.getChildren().addAll(filtres, lineChart, barChart, pieChart, retourBtn);

        updateCharts();

        Scene scene = new Scene(root, 1000, 850);
        stage.setTitle("Statistiques avancées");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Met à jour les trois graphiques (ventes, commandes, répartition)
     * en fonction des filtres appliqués.
     */
    private void updateCharts() {
        ArticleDAO articleDAO = new ArticleDAO();
        CommandeDAO commandeDAO = new CommandeDAO();
        LigneCommandeDAO ligneDAO = new LigneCommandeDAO();

        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();
        String marqueFiltre = marqueCombo.getValue();

        // Filtrage des articles selon la marque
        List<Article> articles = articleDAO.findAll();
        if (!"Toutes marques".equals(marqueFiltre)) {
            articles = articles.stream()
                    .filter(a -> marqueFiltre.equals(a.getMarque()))
                    .toList();
        }
        Set<Integer> idsArticlesFiltres = articles.stream()
                .map(Article::getIdArticle)
                .collect(Collectors.toSet());

        // Filtrage des commandes par date
        List<Commande> commandes = commandeDAO.findAll().stream()
                .filter(c -> {
                    LocalDate d = c.getDate().toLocalDate();
                    boolean matchDebut = (debut == null || !d.isBefore(debut));
                    boolean matchFin = (fin == null || !d.isAfter(fin));
                    return matchDebut && matchFin;
                }).toList();

        // Graphe des ventes (€) par jour
        Map<LocalDate, Double> ventesParDate = new TreeMap<>();
        for (Commande c : commandes) {
            LocalDate date = c.getDate().toLocalDate();
            ventesParDate.put(date, ventesParDate.getOrDefault(date, 0.0) + c.getMontant());
        }

        XYChart.Series<String, Number> ventesSeries = new XYChart.Series<>();
        ventesParDate.forEach((date, montant) ->
                ventesSeries.getData().add(new XYChart.Data<>(date.toString(), montant)));

        lineChart.getData().clear();
        lineChart.setTitle("Ventes par jour");
        lineChart.getData().add(ventesSeries);
        lineChart.setLegendVisible(false);

        // Histogramme des commandes par jour
        Map<LocalDate, Long> commandesParJour = commandes.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getDate().toLocalDate(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        commandesParJour.forEach((date, count) ->
                barSeries.getData().add(new XYChart.Data<>(date.toString(), count)));

        barChart.getData().clear();
        barChart.setTitle("Commandes par jour");
        barChart.getData().add(barSeries);
        barChart.setLegendVisible(false);

        // Camembert des articles les plus commandés
        List<Integer> lignesCommandes = ligneDAO.getAllArticleIds();
        Map<Integer, Integer> compteur = new HashMap<>();
        for (int id : lignesCommandes) {
            if (idsArticlesFiltres.contains(id)) {
                compteur.put(id, compteur.getOrDefault(id, 0) + 1);
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : compteur.entrySet()) {
            Article a = articleDAO.findById(entry.getKey());
            String nom = (a != null) ? a.getNomArticle() : "Inconnu";
            pieData.add(new PieChart.Data(nom, entry.getValue()));
        }

        pieChart.setData(pieData);
        pieChart.setTitle("Répartition des articles commandés");
    }
}
