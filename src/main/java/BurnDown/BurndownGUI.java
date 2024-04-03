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
import utils.SprintData;
import utils.SprintSelector;
import utils.SprintUtils;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ListView;

public class BurndownGUI extends Application {
    private LineChart<String, Number> lineChart;
    private String sprint;
    private String slugURL;
    private String authToken;
    private Label sprintDetails = new Label("Please select a sprint.");
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private int projectID;
    // Additional parameters for Taiga API
    // private final String taigaApiEndpoint;
    // private final String authToken;
    // private final String sprintLastDate;

    public BurndownGUI(int projectID, String authToken, String slugURL) {
        this.projectID = projectID;
        this.slugURL = slugURL;
        this.authToken = authToken;
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Burndown Chart");

        // ComboBox<String> sprintSelector = new ComboBox<>();
        // sprintSelector.setPromptText("select a sprint");
        // SprintSelector.selectSprint(authToken, slugURL, sprintSelector);
        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        //Needs to be modified
        ListView<String> sprintSelector = new ListView<>();
        sprintSelector.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        SprintSelector.selectSprint(authToken, slugURL, sprintSelector); // This method needs to be adapted to populate
                                                                         // the ListView

        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Points");

        this.lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);

        // sprintSelector.setOnAction(e -> {
        // String selectedSprint = sprintSelector.getValue();
        // if (selectedSprint != null) {
        // sprint = selectedSprint;
        // fetchAndDisplayBurndownData(selectedSprint);
        // } else {
        // clearChart();
        // }
        // });

        

        // Replace the setOnAction with a listener that supports multiple selections
        sprintSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            List<String> selectedSprints = sprintSelector.getSelectionModel().getSelectedItems();
            if (selectedSprints != null && !selectedSprints.isEmpty()) {
                fetchAndDisplayBurndownData(selectedSprints);
            } else {
                clearChart();
            }
        });

        VBox layout = new VBox(10, sprintSelector, sprintDetails, lineChart);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 1000, 1000);
        stage.setScene(scene);
        stage.show();
    }

    // private void fetchAndDisplayBurndownData(String selectedSprint) {
    // List<BurnDownDataPoint> progress = Burndown.getBurnDownProgress(authToken,
    // "https://api.taiga.io/api/v1", projectID, selectedSprint);
    // SprintData stats = SprintUtils.getSprintDetails(authToken,
    // "https://api.taiga.io/api/v1", projectID, selectedSprint);
    // Platform.runLater(() -> {
    // if (stats == null || progress == null || progress.isEmpty()) {
    // AlertPopup.showAlert("Error", "No data available for the selected sprint or
    // sprint has not started.");
    // clearChart();
    // } else {
    // updateChart(progress);
    // sprintDetails.setText("Data for " + selectedSprint);

    // // Update the x-axis categories based on the new data
    // List<String> dayLabels = progress.stream()
    // .map(BurnDownDataPoint::getDay)
    // .collect(Collectors.toList());
    // xAxis.setCategories(FXCollections.observableArrayList(dayLabels));
    // xAxis.setTickLabelRotation(90);
    // }
    // });
    // }

    private void fetchAndDisplayBurndownData(List<String> selectedSprints) {
        List<BurnDownDataPoint> progress = Burndown.getBurnDownProgress(authToken, "https://api.taiga.io/api/v1",
                projectID, selectedSprints);

        Platform.runLater(() -> {
            if (progress == null || progress.isEmpty()) {
                AlertPopup.showAlert("Error",
                        "No data available for the selected sprints or sprints have not started.");
                clearChart();
            } else {
                updateChart(progress);
                sprintDetails.setText("Data for selected sprints");
            }
        });
    }

    private void clearChart() {
        this.lineChart.getData().clear();
        sprintDetails.setText("Invalid or not started sprint.");
    }

    private void updateChart(List<BurnDownDataPoint> dataPoints) {
        this.lineChart.getData().clear();

        XYChart.Series<String, Number> openPointsSeries = new XYChart.Series<>();
        openPointsSeries.setName("Open Points");
        XYChart.Series<String, Number> optimalPointsSeries = new XYChart.Series<>();
        optimalPointsSeries.setName("Optimal Points");

        // Populate series with data points
        for (BurnDownDataPoint dataPoint : dataPoints) {
            XYChart.Data<String, Number> openData = new XYChart.Data<>(dataPoint.getDay(), dataPoint.getOpenPoints());
            XYChart.Data<String, Number> optimalData = new XYChart.Data<>(dataPoint.getDay(),
                    dataPoint.getOptimalPoints());

            // Add data to series
            openPointsSeries.getData().add(openData);
            optimalPointsSeries.getData().add(optimalData);

            // Tooltips for Open Points
            openData.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip.install(newValue,
                            new Tooltip("Open Points on " + dataPoint.getDay() + ": " + dataPoint.getOpenPoints()));
                }
            });
            optimalData.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip.install(newValue, new Tooltip(
                            "Optimal Points on " + dataPoint.getDay() + ": " + dataPoint.getOptimalPoints()));
                }
            });
        }
        this.lineChart.getData().addAll(openPointsSeries, optimalPointsSeries);
    }
}
