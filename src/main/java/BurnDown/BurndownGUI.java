package BurnDown;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.SprintData;

public class BurndownGUI extends Application {
    private List<BurnDownDataPoint> dataPoints;
    private LineChart<String, Number> lineChart;
    private String sprint;
    private SprintData sprintData;
    private List<BurnDownDataPoint> progress;

    // Additional parameters for Taiga API
    // private final String taigaApiEndpoint;
    // private final String authToken;
    // private final String sprintLastDate;

    public BurndownGUI(SprintData sprintData,List<BurnDownDataPoint> progress , String sprint) {
        this.sprintData = sprintData;
        this.sprint = sprint;    
        this.progress = progress;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Points");

    }
     

    @Override
    public void start(Stage stage) {
        stage.setTitle("Burndown Chart for "+ sprint);
        Label sprintDetails = new Label("Data for " + sprint);
        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sprintDetails.setAlignment(Pos.CENTER);

        getDataPoints();

        ObservableList<XYChart.Series<String, Number>> seriesList = getSeriesList();
        this.lineChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        lineChart.setData(seriesList);
        lineChart.setLegendVisible(true);

        HBox chartBox = new HBox(15, lineChart);
        chartBox.setTranslateX(15);
        Scene scene = new Scene(new HBox(1,sprintDetails, chartBox,lineChart), 800, 600);
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
        try{

            for (int i = 0; i < dataPoints.size(); i++) {
                BurnDownDataPoint dataPoint = dataPoints.get(i);

                // Open Points
                XYChart.Data<String, Number> openPointsData = new XYChart.Data<>(String.valueOf(i + 1), dataPoint.getOpenPoints());
                openPointsSeries.getData().add(openPointsData);

                // Optimal Points
                XYChart.Data<String, Number> optimalPointsData = new XYChart.Data<>(String.valueOf(i + 1), dataPoint.getOptimalPoints());
                optimalPointsSeries.getData().add(optimalPointsData);
            }

            seriesList.add(openPointsSeries);
            seriesList.add(optimalPointsSeries);
        } catch ( Exception e ) {
            e.printStackTrace();
        }


        return seriesList;

    }

    private void getDataPoints(){

        try{
            if(progress == null){
                throw new IllegalArgumentException("Sprint has not started");
            }
            this.dataPoints = progress;
        }
        catch(Exception e){
            showAlert("Error", "Please Try a Sprint which has been started.");
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }  
}
