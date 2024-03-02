package TaskDefectDensity;

import java.util.NoSuchElementException;
import java.util.Objects;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertPopup;

public class TaskDefectDensityGUI extends Application {
    private int numberOfDeletedTasks;
    private int numberOfUnfinishedTasks;
    private int numberOfTotalTasks;
    private double taskDefectDensity;
    private Label valueTDD;
    private boolean validSprint;

    public TaskDefectDensityGUI(int deletedTasks, int unfinishedTasks, int totalTasks, double taskDefectDensity, boolean validSprint){
        this.numberOfDeletedTasks = deletedTasks;
        this.numberOfUnfinishedTasks = unfinishedTasks;
        this.numberOfTotalTasks = totalTasks;
        this.taskDefectDensity = taskDefectDensity;
        this.validSprint = validSprint;
        
    }

    @Override
    public void start(Stage stage) {
        try{

            if(!validSprint){
                throw new NoSuchElementException();
            }

            stage.setTitle("Task Defect Density");

            ObservableList<PieChart.Data> pieChartData = getChartData();

            PieChart pieChart = new PieChart(pieChartData);

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

            Label taskDefectDensityValue = new Label("Task Defect Density (in percentage): ");
            taskDefectDensityValue.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            valueTDD = new Label("0");
            valueTDD.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            valueTDD.setText(String.format("%.2f", this.taskDefectDensity));

            HBox taskDefectDensity = new HBox(10);
            taskDefectDensity.getChildren().addAll(taskDefectDensityValue, valueTDD);
            taskDefectDensity.setAlignment(Pos.CENTER);

            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(taskDefectDensity, pieChart);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);

            stage.show();
        } catch (NoSuchElementException exception){
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }
    }

    private ObservableList<PieChart.Data> getChartData() {

        
        //Just an example task defect density visualization - to be finished with data from API calls
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Unfinished Tasks", numberOfUnfinishedTasks),
                new PieChart.Data("Removed Tasks", numberOfDeletedTasks),
                new PieChart.Data("Finished Tasks", numberOfTotalTasks - numberOfDeletedTasks-numberOfUnfinishedTasks)
        );

        return pieChartData;
    }

}
