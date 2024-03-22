package TaskDefectDensity;

import java.util.NoSuchElementException;

import javafx.application.Application;
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

import static utils.DisplayPage.projectID;

public class TaskDefectDensityGUI extends Application {
    private int numberOfDeletedTasks;
    private int numberOfUnfinishedTasks;
    private int numberOfTotalTasks;
    private double taskDefectDensity;
    private Label valueTDD;
    private boolean validSprint;
    private String slugURL;
    private String authToken;
    private String sprint;
    private Label sprintDetails;
    private PieChart pieChart;

    public TaskDefectDensityGUI(int deletedTasks, int unfinishedTasks, int totalTasks, double taskDefectDensity, boolean validSprint, String authToken, String slugURL){
        this.numberOfDeletedTasks = deletedTasks;
        this.numberOfUnfinishedTasks = unfinishedTasks;
        this.numberOfTotalTasks = totalTasks;
        this.taskDefectDensity = taskDefectDensity;
        this.validSprint = validSprint;
        this.authToken = authToken;
        this.slugURL = slugURL;
    }

    @Override
    public void start(Stage stage) {
        try{

            if(!validSprint){
                throw new NoSuchElementException();
            }

            stage.setTitle("Task Defect Density");

            ComboBox<String> sprintSelector = new ComboBox<>();
            sprintSelector.setPromptText("Select a sprint");
            SprintSelector.selectSprint(authToken, slugURL, sprintSelector);

            sprintDetails = new Label("Please select a sprint.");
            sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            sprintSelector.setOnAction(e -> {
                String selectedSprint = sprintSelector.getValue();
                if (selectedSprint != null) {
                    this.sprint = selectedSprint;
                    fetchAndUpdateTaskDefectDensityData(selectedSprint); // New method to fetch data and update the UI
                } else {
                    sprintDetails.setText("No sprint selected.");
                }
            });

            // Initialize the PieChart with placeholder data
            pieChart = new PieChart();
            pieChart.setData(FXCollections.observableArrayList());

            Label taskDefectDensityValue = new Label("Task Defect Density (in percentage): ");
            taskDefectDensityValue.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            valueTDD = new Label("0");
            valueTDD.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            HBox taskDefectDensity = new HBox(10);
            taskDefectDensity.getChildren().addAll(taskDefectDensityValue, valueTDD);
            taskDefectDensity.setAlignment(Pos.CENTER);

            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(sprintSelector, sprintDetails, taskDefectDensity, pieChart);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
        } catch (NoSuchElementException exception){
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }
    }

    private void fetchAndUpdateTaskDefectDensityData(String sprint) {
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        TaskDefectDensity tdd = new TaskDefectDensity(authToken,TAIGA_API_ENDPOINT,projectID,sprint);
        if (!tdd.getValidSprint()) {
            AlertPopup.showAlert("Sprint Validation", "Selected sprint is invalid or has not started.");
            clearUI(); // Clear or reset the UI components if the sprint is invalid
            return;
        }

        updateUIWithTaskDefectDensityData(tdd);
    }

    private void updateUIWithTaskDefectDensityData(TaskDefectDensity tdd) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Unfinished Tasks", tdd.getNumberOfUnfinishedTasks()),
                new PieChart.Data("Removed Tasks", tdd.getNumberOfDeletedTasks()),
                new PieChart.Data("Finished Tasks", tdd.getNumberOfTotalTasks() - tdd.getNumberOfDeletedTasks() - tdd.getNumberOfUnfinishedTasks())
        );

        pieChart.setData(pieChartData);
        pieChartData.forEach(data ->
                Tooltip.install(data.getNode(), new Tooltip(data.getName() + ": " + (int)data.getPieValue()))
        );
        valueTDD.setText(String.format("%.2f%%", tdd.getTaskDefectDensity()));
        sprintDetails.setText("Data for " + sprint);
    }

    private void clearUI() {
        pieChart.setData(FXCollections.observableArrayList());
        valueTDD.setText("N/A");
        sprintDetails.setText("Invalid or not started sprint.");
    }


}

