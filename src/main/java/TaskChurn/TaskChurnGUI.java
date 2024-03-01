package TaskChurn;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class TaskChurnGUI extends Application {


    private TreeMap<LocalDate,Float> taskChurnMap;
    private String selectedSprint;

    public TaskChurnGUI(String authToken, String taigaApiEndpoint, int projectID, String selectedSprint) {
        this.selectedSprint = selectedSprint;
        this.taskChurnMap = TaskChurn.calculateTaskChurn(projectID, authToken, taigaApiEndpoint, selectedSprint);
        try {
            if(this.taskChurnMap==null){
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException ex) {
            showAlert("Error", "Please Try a Sprint which has been started.");
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
