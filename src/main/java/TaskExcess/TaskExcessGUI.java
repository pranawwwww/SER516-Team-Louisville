package TaskExcess;

import java.util.NoSuchElementException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertPopup;
import utils.SprintSelector;

public class TaskExcessGUI extends Application {
    private double taskExcessRatio;
    private double newTasks;
    private int totalTasks;
    private boolean validSprint;
    private Label valueTE;
    private String slugURL;
    private int projectId;
    private String authToken;
    private String TAIGA_API_ENDPOINT;
    private String sprint;
    private PieChart pieChart;
    private ComboBox<String> sprintSelector;
    private final StringProperty selectedSprint = new SimpleStringProperty();
    private final StringProperty sprintDetailsText = new SimpleStringProperty("Please select a sprint.");

    public TaskExcessGUI(String authToken, String taigaApiEndpoint, int projectID, String slugURL) {
        this.slugURL = slugURL;
        this.projectId = projectID;
        this.authToken = authToken;
        this.TAIGA_API_ENDPOINT = taigaApiEndpoint;

        // Initialize pieChart
        pieChart = new PieChart();
        pieChart.setTitle("Task Excess");
    }

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Task Excess");

            // Create sprint selector
            sprintSelector = new ComboBox<>();
            sprintSelector.setPromptText("select a sprint");
            sprintSelector.getItems().clear();
            sprintSelector.valueProperty().bindBidirectional(selectedSprint);
            SprintSelector.selectSprint(authToken, slugURL, sprintSelector);
            Label selectSprintLabel = new Label("Select a sprint ");
            selectSprintLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            selectSprintLabel.textProperty().bind(sprintDetailsText);

            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(selectSprintLabel, sprintSelector, pieChart);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();

        } catch (NoSuchElementException exception) {
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }

        // Bind properties with UI elements
        selectedSprint.addListener((observable, oldValue, newValue) -> {
            sprint = newValue;
            getSprintData();
        });

        // Update UI elements when properties change
        selectedSprint.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayChart();
            }
        });
    }

    private void getSprintData() {
        TaskExcess taskExcess = new TaskExcess(authToken, TAIGA_API_ENDPOINT, projectId, sprint);
        this.taskExcessRatio = taskExcess.getTaskExcess();
        this.newTasks = taskExcess.getNewTasks();
        this.totalTasks = taskExcess.getNumberOfTotalTasks();
        this.validSprint = taskExcess.getValidSprint();

        // Update sprint details text
        if (validSprint) {
            sprintDetailsText.set("Data for " + sprint);
        } else {
            sprintDetailsText.set("Invalid or not started sprint.");
        }
    }

    private void displayChart() {
        if (!validSprint) {
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
            return;
        }

        // Create chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("New Tasks", newTasks),
                new PieChart.Data("Other Tasks", totalTasks - newTasks)
        );

        // Update PieChart with data
        pieChart.setData(pieChartData);

        // Add tooltips to PieChart data
        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip(String.format("%s: %.2f", data.getName(), data.getPieValue()));
            Tooltip.install(data.getNode(), tooltip);

            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setStyle("-fx-cursor: hand;");
            });

            data.getNode().setOnMouseExited(event -> {
                data.getNode().setStyle("");
            });
        }

        // Update Task Excess Ratio value
        if (valueTE == null) {
            Label taskExcessLabel = new Label("Task Excess Ratio");
            taskExcessLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            valueTE = new Label("0");
            valueTE.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            HBox taskExcessBox = new HBox(10);
            taskExcessBox.getChildren().addAll(taskExcessLabel, valueTE);
            taskExcessBox.setAlignment(Pos.CENTER);

            VBox root = (VBox) pieChart.getParent();
            root.getChildren().addAll(taskExcessBox);
        }

        valueTE.setText(String.format("%.2f", this.taskExcessRatio));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
