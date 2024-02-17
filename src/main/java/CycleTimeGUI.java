import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


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
    private Button displayButton;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private ScatterChart<String, Number> scatterChart;


    public CycleTimeGUI(int projectID,String authToken) {
        this.projectID = projectID;
        this.authToken = authToken;
    }



    public static void main(String[] args) {
//        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label sprintHolder = new Label("Enter Sprint Number below (ex: \"Sprint 1\")");
        sprintHolder.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sprintInput = new TextField("Enter Sprint");
        sprintInput.setPrefWidth(150);
        sprintInput.setMaxWidth(150);

        this.xAxis = new CategoryAxis();
        this.yAxis = new NumberAxis();
        this.scatterChart = new ScatterChart<>(xAxis, yAxis);
        this.scatterChart.setPrefSize(1200, 1000);

        xAxis.setLabel("Date");
        yAxis.setLabel("Cycle Time");
        
        scatterChart.setLegendVisible(false);

        displayButton = new Button("Display");

        displayButton.setOnAction(e -> displayChart());

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(sprintHolder,sprintInput, displayButton, scatterChart);
        Scene scene = new Scene(root, 1500, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cycle Time Chart Display");
        primaryStage.show();
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

        //Prepare XYChart.Series objects by setting data
        XYChart.Series series = new XYChart.Series();

        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        Map<String, List<Integer>> orderedCycleTime = CycleTime.getMatrixData(projectID, authToken,TAIGA_API_ENDPOINT);

        for (Map.Entry<String, List<Integer>> entry : orderedCycleTime.entrySet()) {
            String key = entry.getKey();
            List<Integer> values = entry.getValue();
            for (Integer value : values) {
                series.getData().add(new XYChart.Data(key,value));
            }
        }
        //Setting the data to scatter chart
        scatterChart.getData().addAll(series);
    }
    private void getSprintData(){
        String sprint = sprintInput.getText();
        this.TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        List<JsonNode> sprintDetails = Burndown.getMilestoneStats(authToken, TAIGA_API_ENDPOINT, projectID, sprint);

        firstDate = sprintDetails.get(1).asText();
        lastDate = sprintDetails.get(2).asText();

        new CycleTimeGUI(projectID,authToken);
    }
}
