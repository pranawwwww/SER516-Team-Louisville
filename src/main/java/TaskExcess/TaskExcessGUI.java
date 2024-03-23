package TaskExcess;

import java.util.NoSuchElementException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertPopup;

public class TaskExcessGUI extends Application {
    private double taskExcessRatio;
    private double newTasks;
    private int totalTasks;
    private boolean validSprint;
    private Label valueTE;

    public TaskExcessGUI(String authToken, String taigaApiEndpoint, int projectID, String selectedSprint){
        TaskExcess taskExcess= new TaskExcess(authToken, taigaApiEndpoint, projectID, selectedSprint);
        this.taskExcessRatio = taskExcess.getTaskExcess();
        this.newTasks = taskExcess.getNewTasks();
        this.totalTasks = taskExcess.getNumberOfTotalTasks();
        this.validSprint = taskExcess.getValidSprint();
        System.out.println("New tasks"+ this.newTasks);
        System.out.println("Total tasks"+ this.totalTasks);        
    }

    @Override
    public void start(Stage stage) {
        try{

            if(!validSprint){
                throw new NoSuchElementException();
            }

            stage.setTitle("Task Excess");

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

            Label taskExcessLabel = new Label("Task Excess Ratio");
            taskExcessLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            valueTE = new Label("0");
            valueTE.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            valueTE.setText(String.format("%.2f", this.taskExcessRatio));

            HBox taskExcessBox = new HBox(10);
            taskExcessBox.getChildren().addAll(taskExcessLabel, valueTE);
            taskExcessBox.setAlignment(Pos.CENTER);

            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(taskExcessBox, pieChart);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);

            stage.show();
        } catch (NoSuchElementException exception){
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }
    }

    private ObservableList<PieChart.Data> getChartData() {        
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("New Tasks", (int)this.newTasks),
                new PieChart.Data("Other Tasks", totalTasks - (int)this.newTasks)
                
        );

        return pieChartData;
    }
}
