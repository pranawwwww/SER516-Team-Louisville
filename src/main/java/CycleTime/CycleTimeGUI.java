package CycleTime;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertPopup;
import utils.SprintData;
import utils.SprintUtils;

import org.apache.commons.lang3.tuple.Pair;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class CycleTimeGUI extends Application {

    private int projectID;
    private String authToken;
    private String TAIGA_API_ENDPOINT;
    private String firstDate;
    private String lastDate;
    private TextField sprintInput;
    private ComboBox<String> sprintComboBox;
    private Button displayButton;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private ScatterChart<String, Number> scatterChart;
    private Label valueCycleTime;
    private Label valueTaskCompleted;
    private Float computeCycleTime;
    private Integer numberTaskCompleted;
    private String sprint;
    private Label sprintDetails;


    public CycleTimeGUI(int projectID,String authToken, String sprint) {
        this.projectID = projectID;
        this.authToken = authToken;
        this.sprint = sprint;
    }

    public static void main(String[] args) {
//        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        sprintDetails = new Label("Data for " + sprint);
        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
        this.scatterChart = new ScatterChart<>(xAxis, yAxis);
        this.scatterChart.setPrefSize(1200, 1000);

        xAxis.setLabel("Date");
        yAxis.setLabel("Cycle Time");
        
        scatterChart.setLegendVisible(false);

        Label avgCycleTime = new Label("Average Cycle Time in Days: ");
        avgCycleTime.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        valueCycleTime = new Label("0");
        valueCycleTime.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label taskCompleted = new Label("Number of Tasks Completed: ");
        taskCompleted.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        valueTaskCompleted = new Label("0");
        valueTaskCompleted.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");


        HBox cycleTime = new HBox(10);
        cycleTime.getChildren().addAll(avgCycleTime,valueCycleTime);
        cycleTime.setAlignment(Pos.CENTER);

        HBox tasks = new HBox(10);
        tasks.getChildren().addAll(taskCompleted,valueTaskCompleted);
        tasks.setAlignment(Pos.CENTER);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(sprintDetails, cycleTime, tasks, scatterChart);
        Scene scene = new Scene(root, 1500, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cycle Time Chart Display " + sprint);
        primaryStage.show();

        displayChart();
    }
    private void displayChart(){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        getSprintData();
        int start_year = Integer.parseInt(firstDate.substring(0,4));
        int last_year = Integer.parseInt(lastDate.substring(0,4));
        int start_month = Integer.parseInt(firstDate.substring(5,7));
        int last_month = Integer.parseInt(lastDate.substring(5,7));
        int start_date = Integer.parseInt(firstDate.substring(8));
        int last_date = Integer.parseInt(lastDate.substring(8));

        LocalDate startDate = LocalDate.of(start_year, start_month, start_date);
        LocalDate endDate = LocalDate.of(last_year, last_month, last_date);

        ObservableList<String> dateList = FXCollections.observableArrayList();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateList.add(currentDate.format(dateFormatter));
            currentDate = currentDate.plusDays(1);
        }

        xAxis.setCategories(FXCollections.observableArrayList(dateList));
        xAxis.setTickLabelRotation(90);

        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        Map<String, List<Pair<String, Integer>>> orderedCycleTime = CycleTime.getMatrixData(projectID, authToken,TAIGA_API_ENDPOINT,sprint);

        computeCycleTime = 0.0f;
        numberTaskCompleted =0;
        for (Map.Entry<String, List<Pair<String, Integer>>> entry : orderedCycleTime.entrySet()) {
            String key = entry.getKey();
            List<Pair<String, Integer>> pairs = entry.getValue();
            for (Pair<String, Integer> pair : pairs) {
                String subject = pair.getLeft(); // Extracting subject from Pair
                Integer value = pair.getRight(); // Extracting integer value from Pair
                computeCycleTime += value;
                ++numberTaskCompleted;
                XYChart.Series<String, Number> dataPoint = new XYChart.Series<>();
                XYChart.Data<String, Number> data = new XYChart.Data<>(key, (Number) value);
                dataPoint.getData().add(data);
                scatterChart.getData().add(dataPoint);
                Tooltip tooltip = new Tooltip(subject);
                Tooltip.install(data.getNode(), tooltip);
            }
        }
        computeCycleTime/=numberTaskCompleted;
        valueCycleTime.setText(String.format("%.2f", computeCycleTime));
        valueTaskCompleted.setText(String.valueOf(numberTaskCompleted));
    }
    private void getSprintData(){

        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        try{
            SprintData sprintDetails = SprintUtils.getSprintDetails(authToken, TAIGA_API_ENDPOINT, projectID, sprint);

            firstDate = sprintDetails.getStart_date();
            lastDate = sprintDetails.getEnd_date();
    
            // new CycleTimeGUI(projectID,authToken,sprints);
        }
        catch(Exception e){
            AlertPopup.showAlert("Error", "Please Try a Done Sprint .");
        }

    }
}
