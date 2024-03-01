package TaskDefectDensity;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TaskDefectDensityGUI extends Application {
    private int numberOfDeletedTasks;
    private int numberOfUnfinishedTasks;
    private int numberOfTotalTasks;
    private float taskDefectDensity;
    private Label valueTDD;

    public TaskDefectDensityGUI(int deletedTasks, int unfinishedTasks, int totalTasks, float taskDefectDensity){
        this.numberOfDeletedTasks = deletedTasks;
        this.numberOfUnfinishedTasks = unfinishedTasks;
        this.numberOfTotalTasks = totalTasks;
        this.taskDefectDensity = taskDefectDensity;
        
    }

    @Override
    public void start(Stage stage) {
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

        Scene scene = new Scene(pieChart, 800, 600);
        stage.setScene(scene);

        stage.show();
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
