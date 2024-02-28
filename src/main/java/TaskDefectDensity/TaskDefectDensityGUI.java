package TaskDefectDensity;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TaskDefectDensityGUI extends Application {

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

        Scene scene = new Scene(pieChart, 800, 600);
        stage.setScene(scene);

        stage.show();
    }

    private ObservableList<PieChart.Data> getChartData() {
        //Just an example task defect density visualization - to be finished with data from API calls
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Unfinished Tasks", 30),
                new PieChart.Data("Removed Tasks", 25),
                new PieChart.Data("Other Tasks", 45)
        );

        return pieChartData;
    }

}
