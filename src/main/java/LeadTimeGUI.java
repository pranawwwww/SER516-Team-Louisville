import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.Map;

public class LeadTimeGUI extends Application {

    private final Map<String, Map<String, Object>> dataMap;
    private final StackedBarChart<String, Number> stackedBarChart;
    private final ToggleButton toggleButton;

    public LeadTimeGUI(Map<String, Map<String, Object>> dataMap) {
        this.dataMap = dataMap;

        // Create StackedBarChart
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        xAxis.setLabel("Lead Time (Days)");
        yAxis.setLabel("Number of Tasks");

        // Set initial Legend visibility
        stackedBarChart.setLegendVisible(false);

        // Create ToggleButton
        this.toggleButton = new ToggleButton("Toggle Legend");
        toggleButton.setOnAction(e -> {
            stackedBarChart.setLegendVisible(toggleButton.isSelected());
        });
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Lead time for all closed tasks in Project");

        ObservableList<XYChart.Series<String, Number>> seriesList = getSeriesList();
        stackedBarChart.setData(seriesList);

        addTooltip(stackedBarChart);

        HBox chartBox = new HBox(15, stackedBarChart);
        chartBox.setTranslateX(15);
        Scene scene = new Scene(new HBox(1, chartBox, toggleButton), 800, 600);
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
                String name = taskData.get("Name").toString();
                String startDate = taskData.get("startDate").toString();
                String endDate = taskData.get("endDate").toString();

                Tooltip tooltip = new Tooltip("Name: " + name + "\nStart Date: " + startDate + "\nEnd Date: " + endDate);
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }

}
