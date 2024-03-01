package TaskChurn;

import java.time.LocalDate;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;

public class TaskChurnGUI extends Application {


    private TreeMap<LocalDate,Float> taskChurnMap;
    private String selectedSprint;

    public TaskChurnGUI(String authToken, String taigaApiEndpoint, int projectID, String selectedSprint) {
        this.taskChurnMap = TaskChurn.calculateTaskChurn(projectID, authToken, taigaApiEndpoint, selectedSprint);
        this.selectedSprint = selectedSprint;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Task Churn Graph for "+this.selectedSprint);
        // Defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        // Setting labels for axes
        xAxis.setLabel("Date");
        yAxis.setLabel("Churn Percentage");
    }
}
