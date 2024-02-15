import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.Map;

public class LeadTimeGUI extends Application {

    private final Map<String, Map<String, Object>> dataMap = getDataMap(); // Your provided HashMap

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Lead time for all closed tasks in Project");

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        xAxis.setLabel("Lead Time (Days)");
        yAxis.setLabel("Number of Tasks");

        ObservableList<XYChart.Series<String, Number>> seriesList = getSeriesList();
        stackedBarChart.setData(seriesList);

        addTooltip(stackedBarChart);

        Scene scene = new Scene(stackedBarChart, 800, 600);
        stage.setScene(scene);

        stage.show();
    }

    private ObservableList<XYChart.Series<String, Number>> getSeriesList() {
        ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();

        for (String taskName : dataMap.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(taskName);

            Map<String, Object> taskData = dataMap.get(taskName);
            long leadTimeInDays = (long) taskData.get("leadTimeInDays");

            XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(leadTimeInDays), 1);
            series.getData().add(data);

            seriesList.add(series);
        }

        // Sort the series based on x-axis values (days)
        seriesList.sort(Comparator.comparing(series -> Double.parseDouble(series.getData().get(0).getXValue())));

        return seriesList;
    }

    private void addTooltip(StackedBarChart<String, Number> chart) {
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Map<String, Object> taskData = dataMap.get(series.getName());
                String startDate = taskData.get("startDate").toString();
                String endDate = taskData.get("endDate").toString();

                Tooltip tooltip = new Tooltip("Start Date: " + startDate + "\nEnd Date: " + endDate);
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }

    private Map<String, Map<String, Object>> getDataMap() {
        // Replace this method with your actual data retrieval mechanism
        int projectId = 1520578;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, Map<String, Object>> input = LeadTime.getLeadTimePerTask(projectId, authToken, TAIGA_API_ENDPOINT);
        return input;
    }
}
