package LeadTime;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.AlertPopup;
import utils.SprintData;
import utils.SprintSelector;
import utils.SprintUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class LeadTimeGUI extends Application {

    private Map<String, Map<String, Object>> dataMap = new HashMap<>();
    private final StackedBarChart<String, Number> stackedBarChart;
    private final IntegerProperty projectID = new SimpleIntegerProperty();
    private final StringProperty authToken = new SimpleStringProperty();
    private final StringProperty taigaApiEndpoint = new SimpleStringProperty();
    private final StringProperty slugURL = new SimpleStringProperty();
    private final StringProperty sprint = new SimpleStringProperty();
    private final StringProperty firstDate = new SimpleStringProperty();
    private final StringProperty lastDate = new SimpleStringProperty();
    private final Label sprintDetails = new Label();

    public LeadTimeGUI(int projectID, String authToken, String taigaApiEndpoint, String slugURL) {
        this.projectID.set(projectID);
        this.authToken.set(authToken);
        this.taigaApiEndpoint.set(taigaApiEndpoint);
        this.slugURL.set(slugURL);

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

        Label selectSprintLabel = new Label("Select a sprint ");
        selectSprintLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        RadioButton selectBySprintRadio = new RadioButton("Select by Sprint");
        RadioButton selectByDatesRadio = new RadioButton("Select by Dates");

        ToggleGroup toggleGroup = new ToggleGroup();
        selectBySprintRadio.setToggleGroup(toggleGroup);
        selectByDatesRadio.setToggleGroup(toggleGroup);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select start date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select end date");

        ComboBox<String> sprintSelector = new ComboBox<>();
        sprintSelector.setPromptText("select a sprint");
        sprintSelector.getItems().clear();
        SprintSelector.selectSprint(authToken.get(), slugURL.get(), sprintSelector);

        sprintSelector.setVisible(false);
        startDatePicker.setVisible(false);
        endDatePicker.setVisible(false);

        VBox radioButtons = new VBox(10);
        radioButtons.getChildren().addAll(selectBySprintRadio, selectByDatesRadio);
        radioButtons.setAlignment(Pos.CENTER);

        selectBySprintRadio.setOnAction(e -> {
            sprintSelector.setVisible(true);
            startDatePicker.setVisible(false);
            endDatePicker.setVisible(false);
        });

        selectByDatesRadio.setOnAction(e -> {
            sprintSelector.setVisible(false);
            startDatePicker.setVisible(true);
            endDatePicker.setVisible(true);
        });

        selectBySprintRadio.setSelected(true); // Select by sprint by default

        selectBySprintRadio.fire(); // Trigger event to show sprint selector initially

        sprintSelector.setOnAction(e -> {
            sprint.set(sprintSelector.getValue());
            // Reset dataMap to null when a new sprint is selected
            dataMap = null;
        });

        Button selectSprintBtn = new Button("Display Chart");

        selectSprintBtn.setOnAction(e -> {
            if (selectBySprintRadio.isSelected()) {
                String selectedSprint = sprintSelector.getValue();
                if (selectedSprint != null) {
                    sprint.set(selectedSprint);
                    sprintDetails.setText("Data for " + sprint.get());
                    stackedBarChart.getData().clear();
                    SprintData sprintDetails = SprintUtils.getSprintDetails(authToken.get(), taigaApiEndpoint.get(), projectID.get(), selectedSprint);
                    firstDate.set(sprintDetails.getStart_date());
                    lastDate.set(sprintDetails.getEnd_date());
                    dataMap = LeadTime.getLeadTimePerTask(projectID.get(), authToken.get(), taigaApiEndpoint.get(), firstDate.get(), lastDate.get());
                    if (dataMap.isEmpty()) {
                        AlertPopup.showAlert("Error", "No data available for the selected sprint.");
                    } else {
                        displayChart(stage);
                    }
                } else {
                    AlertPopup.showAlert("Error", "Please select a sprint.");
                }
            } else if (selectByDatesRadio.isSelected()) {
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                if (startDate != null && endDate != null) {
                    stackedBarChart.getData().clear();
                    dataMap = LeadTime.getLeadTimePerTask(projectID.get(), authToken.get(), taigaApiEndpoint.get(), startDate.toString(), endDate.toString());
                    if (dataMap.isEmpty()) {
                        AlertPopup.showAlert("Error", "No data available for the selected date range.");
                    } else {
                        displayChart(stage);
                    }
                } else {
                    AlertPopup.showAlert("Error", "Please select both start and end dates.");
                }
            }
        });

        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sprintDetails.setAlignment(Pos.CENTER);

        HBox chartBox = new HBox(stackedBarChart);
        chartBox.setAlignment(Pos.CENTER);

        // Create the VBox containing the controls and chartBox
        VBox controlsAndChartBox = new VBox(10, selectSprintLabel, radioButtons, sprintSelector, startDatePicker, endDatePicker, selectSprintBtn, sprintDetails, chartBox);
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
            if (dataMap.isEmpty()) {
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
//            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }

        return seriesList;
    }

    private void addTooltip(StackedBarChart<String, Number> chart) {
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Map<String, Object> taskData = dataMap.get(series.getName());
                String taskName = taskData.get("taskName").toString();
                String userStoryName = taskData.get("userStoryName").toString();
                String epicName = taskData.get("epicName").toString();
                String startDate = taskData.get("startDate").toString();
                String endDate = taskData.get("endDate").toString();

                Tooltip tooltip = new Tooltip("Epic Name: " + epicName + "\nUS Name: " + userStoryName + "\nTask Name: "
                        + taskName + "\nStart Date: " + startDate + "\nEnd Date: " + endDate);
                tooltip.setHideOnEscape(true);
                tooltip.setHideDelay(Duration.seconds(20));
                Tooltip.install(data.getNode(), tooltip);
            }
        }
    }
}
