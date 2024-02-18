import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BurndownGUI extends Application {
    private final List<BurnDownDataPoint> dataPoints;
    private LineChart<String, Number> lineChart;
    private final ToggleButton toggleButton;

    // Additional parameters for Taiga API
    // private final String taigaApiEndpoint;
    // private final String authToken;
    // private final String sprintLastDate;

    public BurndownGUI(List<BurnDownDataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        
        // Create LineChart
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //this.lineChart = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("Day");
        yAxis.setLabel("Points");

        // Create ToggleButton
        this.toggleButton = new ToggleButton("Toggle Legend");
        toggleButton.setOnAction(e -> {
            lineChart.setLegendVisible(toggleButton.isSelected());
        });
    }
     

    @Override
    public void start(Stage stage) {
        stage.setTitle("Burndown Chart");

        ObservableList<XYChart.Series<String, Number>> seriesList = getSeriesList();
        lineChart.setData(seriesList);

        HBox chartBox = new HBox(15, lineChart);
        chartBox.setTranslateX(15);
        Scene scene = new Scene(new HBox(1, chartBox, toggleButton), 800, 600);
        stage.setScene(scene);

        stage.show();
    }

    private ObservableList<XYChart.Series<String, Number>> getSeriesList() {
        ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();

        // Series for Open Points
        XYChart.Series<String, Number> openPointsSeries = new XYChart.Series<>();
        openPointsSeries.setName("Open Points");

        // Series for Optimal Points
        XYChart.Series<String, Number> optimalPointsSeries = new XYChart.Series<>();
        optimalPointsSeries.setName("Optimal Points");

        // Assuming dataPoints is sorted by date
        for (int i = 0; i < dataPoints.size(); i++) {
            BurnDownDataPoint dataPoint = dataPoints.get(i);

            // Open Points
            XYChart.Data<String, Number> openPointsData = new XYChart.Data<>(String.valueOf(i + 1), dataPoint.getOpenPoints());
            openPointsSeries.getData().add(openPointsData);

            // Optimal Points
            XYChart.Data<String, Number> optimalPointsData = new XYChart.Data<>(String.valueOf(i + 1), dataPoint.getOptimalPoints());
            optimalPointsSeries.getData().add(optimalPointsData);
        }

        this.lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());

        seriesList.add(openPointsSeries);
        seriesList.add(optimalPointsSeries);

        return seriesList;
    }

    
       
}
