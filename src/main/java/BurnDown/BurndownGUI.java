package BurnDown;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertPopup;
import utils.SprintSelector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BurndownGUI extends Application {
    private LineChart<String, Number> lineChart;
    private final StringProperty selectedSprint = new SimpleStringProperty();
    private final StringProperty sprintDetailsText = new SimpleStringProperty("Please select a sprint.");
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private int projectID;
    private String slugURL;
    private String authToken;

    public BurndownGUI(int projectID, String authToken, String slugURL) {
        this.projectID = projectID;
        this.selectedSprint.set("");
        this.slugURL = slugURL;
        this.authToken = authToken;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Burndown Chart");

        ComboBox<String> sprintSelector = new ComboBox<>();
        sprintSelector.setPromptText("select a sprint");
        sprintSelector.valueProperty().bindBidirectional(Burndown.selectedSprintProperty());
        SprintSelector.selectSprint(this.authToken, this.slugURL, sprintSelector);
        Label sprintDetails = new Label();
        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Bind sprintDetails label text to sprintDetailsText property
        sprintDetails.textProperty().bind(sprintDetailsText);

        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Points");

        this.lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);

        // Bind selectedSprint to fetchAndDisplayBurndownData method
        Burndown.selectedSprintProperty().addListener((obs, oldVal, newVal) -> fetchAndDisplayBurndownData(newVal));

        VBox layout = new VBox(10, sprintSelector, sprintDetails, lineChart);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 1000, 1000);
        stage.setScene(scene);
        stage.show();
    }

    private void fetchAndDisplayBurndownData(String selectedSprint) {
        List<BurnDownDataPoint> progress = Burndown.getBurnDownProgress(this.authToken, "https://api.taiga.io/api/v1", this.projectID, selectedSprint);
        Platform.runLater(() -> {
            if (progress == null || progress.isEmpty()) {
                AlertPopup.showAlert("Error", "No data available for the selected sprint or sprint has not started.");
                clearChart();
            } else {
                updateChart(progress);
                sprintDetailsText.set("Data for " + selectedSprint);

                // Update the x-axis categories based on the new data
                List<String> dayLabels = progress.stream()
                        .map(BurnDownDataPoint::getDay)
                        .collect(Collectors.toList());
                xAxis.setCategories(FXCollections.observableArrayList(dayLabels));
                xAxis.setTickLabelRotation(90);
            }
        });
    }

    private void clearChart() {
        this.lineChart.getData().clear();
        sprintDetailsText.set("Invalid or not started sprint.");
    }

    @SuppressWarnings("unchecked")
    private void updateChart(List<BurnDownDataPoint> dataPoints) {
        this.lineChart.getData().clear();

        XYChart.Series<String, Number> openPointsSeries = new XYChart.Series<>();
        openPointsSeries.setName("Open Points");
        XYChart.Series<String, Number> optimalPointsSeries = new XYChart.Series<>();
        optimalPointsSeries.setName("Optimal Points");

        // Populate series with data points
        for (BurnDownDataPoint dataPoint : dataPoints) {
            XYChart.Data<String, Number> openData = new XYChart.Data<>(dataPoint.getDay(), dataPoint.getOpenPoints());
            XYChart.Data<String, Number> optimalData = new XYChart.Data<>(dataPoint.getDay(), dataPoint.getOptimalPoints());

            // Add data to series
            openPointsSeries.getData().add(openData);
            optimalPointsSeries.getData().add(optimalData);

            // Tooltips for Open Points
            openData.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip.install(newValue, new Tooltip("Open Points on " + dataPoint.getDay() + ": " + dataPoint.getOpenPoints()));
                }
            });
            optimalData.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip.install(newValue, new Tooltip("Optimal Points on " + dataPoint.getDay() + ": " + dataPoint.getOptimalPoints()));
                }
            });
        }
        this.lineChart.getData().addAll(openPointsSeries, optimalPointsSeries);
    }
}