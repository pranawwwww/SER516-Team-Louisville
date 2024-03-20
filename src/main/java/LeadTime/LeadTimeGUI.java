package LeadTime;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.AlertPopup;
import utils.SprintSelector;

import java.util.Comparator;
import java.util.Map;

public class LeadTimeGUI extends Application {

    private Map<String, Map<String, Object>> dataMap;
    private final StackedBarChart<String, Number> stackedBarChart;
    private int projectID;
    private String authToken;
    private String TAIGA_API_ENDPOINT;
    private String slugURL;
    private String sprint;
    private Label sprintDetails = new Label();
    public LeadTimeGUI(int projectID,String authToken,String TAIGA_API_ENDPOINT,String slugURL) {
        if(this.dataMap != null){
            this.dataMap.clear();
        }
        this.projectID = projectID;
        this.authToken = authToken;
        this.TAIGA_API_ENDPOINT = TAIGA_API_ENDPOINT;
        this.slugURL = slugURL;
        // Create StackedBarChart
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
        xAxis.setLabel("Lead Time (Days)");
        yAxis.setLabel("Number of Tasks");

        // Set initial Legend visibility
        stackedBarChart.setLegendVisible(false);

        stackedBarChart.setPrefSize(800, 400);
    }

    @Override
    public void start(Stage stage) {
        ComboBox<String> sprintSelector = new ComboBox<>();
        sprintSelector.setPromptText("select a sprint");
        sprintSelector.getItems().clear();
        SprintSelector.selectSprint(authToken, slugURL, sprintSelector);

        sprintSelector.setOnAction(e -> {
            String selectedSprint = sprintSelector.getValue();
            if (selectedSprint != null) {
                this.sprint = selectedSprint;
                // Reset dataMap to null when a new sprint is selected
                this.dataMap = null;
            }
        });

        Button selectSprintBtn = new Button("Display Chart");

        selectSprintBtn.setOnAction(e -> {
            String selectedSprint = sprintSelector.getValue();
            if (selectedSprint != null) {
                this.sprint = selectedSprint;
                sprintDetails.setText("Data for " + sprint);
                stackedBarChart.getData().clear();
                this.dataMap = LeadTime.getLeadTimePerTask(projectID, authToken,TAIGA_API_ENDPOINT,sprint);
                displayChart(stage);
            }
        });

        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sprintDetails.setAlignment(Pos.CENTER);

        HBox chartBox = new HBox(stackedBarChart);
        chartBox.setAlignment(Pos.CENTER);

        // Create the VBox containing the controls and chartBox
        VBox controlsAndChartBox = new VBox(10, sprintSelector, selectSprintBtn, chartBox);
        controlsAndChartBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(controlsAndChartBox, 800, 600);

        stage.setScene(scene);

        stage.show();

        displayChart(stage);
    }

    private void displayChart(Stage stage) {
        ObservableList<XYChart.Series<String, Number>> seriesList = getSeriesList();
        if (seriesList.isEmpty())
            return;

        stackedBarChart.setData(seriesList);
        addTooltip(stackedBarChart);
    }

    private ObservableList<XYChart.Series<String, Number>> getSeriesList() {

        ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();
        try {
            if(dataMap.isEmpty()){
                throw new IllegalArgumentException("Sprint has not started");
            }
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

        } catch (Exception e) {
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }

        return seriesList;
    }

    private void addTooltip(StackedBarChart<String, Number> chart) {
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Map<String, Object> taskData = dataMap.get(series.getName());
                String taskName = taskData.get("taskName").toString();
                String userStoryName =  taskData.get("userStoryName").toString();
                String epicName =  taskData.get("epicName").toString();
                String startDate = taskData.get("startDate").toString();
                String endDate = taskData.get("endDate").toString();

                Tooltip tooltip = new Tooltip( "Epic Name: " + epicName + "\nUS Name: " + userStoryName +  "\nTask Name: "
                + taskName + "\nStart Date: " + startDate + "\nEnd Date: " + endDate);
                tooltip.setHideOnEscape(true);
                tooltip.setHideDelay(Duration.seconds(20));
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }

}
