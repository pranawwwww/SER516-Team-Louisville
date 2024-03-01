package TaskChurn;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import javafx.scene.Scene;

import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
        stage.setTitle("Task Churn Graph for " + selectedSprint);
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Churn Percentage");

        final LineChart<Number, Number> lineGraph = new LineChart<>(xAxis, yAxis);
        lineGraph.setTitle("Task Churn");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(selectedSprint);

        LocalDate startDate = taskChurnMap.firstKey();
        // Add data to the series
        taskChurnMap.forEach((date, churn) -> {
            long daysBetween = ChronoUnit.DAYS.between(startDate, date);
            series.getData().add(new XYChart.Data<>(daysBetween, churn));
        });

        lineGraph.getData().add(series);

        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                LocalDate tickDate = startDate.plusDays(object.longValue());
                return tickDate.toString();
            }
        });

        Scene scene = new Scene(lineGraph, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
