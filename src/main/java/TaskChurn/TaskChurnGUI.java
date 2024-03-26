package TaskChurn;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.SprintSelector;

public class TaskChurnGUI extends Application {

    private TreeMap<LocalDate, Float> taskChurnMap = new TreeMap<>();
    private String slugURL;
    private String sprint;
    private int projectID;
    private String authToken;
    private String taigaApiEndpoint;
    private LineChart<Number, Number> lineGraph;

    public TaskChurnGUI(String authToken,String TAIGA_API_ENDPOINT,int projectID,String slugURL) {
        this.projectID = projectID;
        this.authToken = authToken;
        this.taigaApiEndpoint = TAIGA_API_ENDPOINT;
        this.slugURL = slugURL;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Task Churn Graph");

        Label selectSprintLabel = new Label("Select a sprint ");
        selectSprintLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> sprintSelector = new ComboBox<>();
        sprintSelector.setPromptText("select a sprint");
        sprintSelector.getItems().clear();
        SprintSelector.selectSprint(authToken,slugURL, sprintSelector);

        sprintSelector.setOnAction(e -> {
            String selectedSprint = sprintSelector.getValue();
            if (selectedSprint != null) {
                this.sprint = selectedSprint;
            }
        });

        Button displayButton = new Button("Display Chart");
        displayButton.setDisable(true); // Initially disable the button

        HBox controls = new HBox(10, selectSprintLabel,sprintSelector, displayButton);
        controls.setAlignment(Pos.CENTER);

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Churn Percentage");

        lineGraph = new LineChart<>(xAxis, yAxis);
        lineGraph.setTitle("Task Churn");

        VBox root = new VBox(10, controls, lineGraph);
        root.setAlignment(Pos.CENTER); // Center align root

        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.show();

        displayButton.setOnAction(e -> {
            String selectedSprint = sprintSelector.getValue();
            if (selectedSprint != null) {
                this.sprint = selectedSprint;
                this.taskChurnMap = TaskChurn.calculateTaskChurn(projectID, authToken, taigaApiEndpoint, selectedSprint);
                displayChart();
            } else {
                showAlert("Error", "Please select a sprint.");
            }
        });

        sprintSelector.setOnAction(e -> {
            displayButton.setDisable(sprintSelector.getValue() == null); // Enable button when sprint is selected
        });
    }

    private void displayChart() {
        try {
            if (this.taskChurnMap.isEmpty()) {
                throw new NoSuchElementException();
            }

            lineGraph.getData().clear(); // Clear previous data

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(sprint);

            LocalDate startDate = taskChurnMap.firstKey();
            // Add data to the series
            taskChurnMap.forEach((date, churn) -> {
                long daysBetween = ChronoUnit.DAYS.between(startDate, date);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(daysBetween, churn);

                Tooltip tooltip = new Tooltip("Date: " + date.toString() + "\nChurn: " + churn + "%");
                tooltip.setShowDelay(Duration.seconds(0));
                Tooltip.install(dataPoint.getNode(), tooltip);

                dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        Tooltip.install(newNode, tooltip);
                    }
                });

                series.getData().add(dataPoint);
            });

            lineGraph.getData().add(series);
        } catch (NoSuchElementException ex) {
            showAlert("Error", "Please Try a Sprint which has been started.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
