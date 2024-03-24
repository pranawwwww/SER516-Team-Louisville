package TaskExcess;

import java.util.NoSuchElementException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
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

    public TaskExcessGUI(String authToken, String taigaApiEndpoint, int projectID, String slugURL){
        this.slugURL = slugURL;
        this.projectId = projectID;
        this.authToken = authToken;
        this.TAIGA_API_ENDPOINT = taigaApiEndpoint;
    }

    @Override
    public void start(Stage stage) {
        try{
            stage.setTitle("Task Excess");

            Label selectSprintLabel = new Label("Select a sprint ");
            selectSprintLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Create sprint selector
            sprintSelector = new ComboBox<>();
            sprintSelector.setPromptText("select a sprint");
            sprintSelector.getItems().clear();
            SprintSelector.selectSprint(authToken,slugURL, sprintSelector);

            // Button to display chart
            Button displayChartBtn = new Button("Display Chart");
            displayChartBtn.setOnAction(e -> {
                String selectedSprint = sprintSelector.getValue();
                if (selectedSprint != null) {
                    this.sprint = selectedSprint;
                    getSprintData();
                    displayChart();
                }
            });

            // Create empty PieChart
            pieChart = new PieChart();
            pieChart.setTitle("Task Excess");

            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(selectSprintLabel,sprintSelector, displayChartBtn, pieChart);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();

        } catch (NoSuchElementException exception){
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }
    }

    private void getSprintData() {

        TaskExcess taskExcess = new TaskExcess(authToken, TAIGA_API_ENDPOINT, projectId, sprint);
        this.taskExcessRatio = taskExcess.getTaskExcess();
        this.newTasks = taskExcess.getNewTasks();
        this.totalTasks = taskExcess.getNumberOfTotalTasks();
        this.validSprint = taskExcess.getValidSprint();
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

        Label taskExcessLabel = new Label("Task Excess Ratio");
        taskExcessLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        valueTE = new Label("0");
        valueTE.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        valueTE.setText(String.format("%.2f", this.taskExcessRatio));

        HBox taskExcessBox = new HBox(10);
        taskExcessBox.getChildren().addAll(taskExcessLabel, valueTE);
        taskExcessBox.setAlignment(Pos.CENTER);

        VBox root = (VBox) pieChart.getParent();
        root.getChildren().addAll(taskExcessBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

