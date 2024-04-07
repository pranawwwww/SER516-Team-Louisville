package CycleTime;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.AlertPopup;
import utils.SprintData;
import utils.SprintSelector;
import utils.SprintUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    private String slugURL;
    private final StringProperty selectedSprint = new SimpleStringProperty();
<<<<<<< HEAD
=======
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
>>>>>>> period-4


    public CycleTimeGUI(int projectID,String authToken, String slug) {
        this.projectID = projectID;
        this.authToken = authToken;
        this.slugURL = slug;
    }

    public static void main(String[] args) {
//        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Label selectByLabel = new Label("Select data by:");
        selectByLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

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
        sprintSelector.setPromptText("Select a sprint");
        sprintSelector.getItems().clear();
        SprintSelector.selectSprint(authToken, slugURL, sprintSelector);

<<<<<<< HEAD
=======

>>>>>>> period-4
        sprintSelector.valueProperty().bindBidirectional(selectedSprint);

        sprintDetails = new Label(); // Initialize sprintDetails label without text
        sprintDetails.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sprintDetails.setVisible(false); // Hide the label initially
<<<<<<< HEAD
=======
      
        // Initially hide both selectors
        sprintSelector.setVisible(false);
        startDatePicker.setVisible(false);
        endDatePicker.setVisible(false);

        VBox radioButtons = new VBox(10);
        radioButtons.getChildren().addAll(selectBySprintRadio, selectByDatesRadio);
        radioButtons.setAlignment(Pos.CENTER);

        // Event handlers for radio buttons
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

>>>>>>> period-4

        sprintSelector.setOnAction(e -> {
            String selectedSprint = sprintSelector.getValue();
            if (selectedSprint != null) {
                this.sprint = selectedSprint;
                sprintDetails.setText("Data for " + sprint); // Update label text
                sprintDetails.setVisible(true);
            } else {
                sprintDetails.setText("No sprint selected.");
                sprintDetails.setVisible(true);
<<<<<<< HEAD
=======
            }
        });

        startDatePicker.setOnAction(e -> {
            LocalDate selectedStartDate = startDatePicker.getValue();
            if (selectedStartDate != null) {
                System.out.println("Start Date: " + selectedStartDate.format(dateFormatter));
            }
        });

        endDatePicker.setOnAction(e -> {
            LocalDate selectedEndDate = endDatePicker.getValue();
            if (selectedEndDate != null) {
                System.out.println("End Date: " + selectedEndDate.format(dateFormatter));
>>>>>>> period-4
            }
        });

        Button selectSprintBtn = new Button("Display Chart");

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
        cycleTime.getChildren().addAll(avgCycleTime, valueCycleTime);
        cycleTime.setAlignment(Pos.CENTER);

        HBox tasks = new HBox(10);
        tasks.getChildren().addAll(taskCompleted, valueTaskCompleted);
        tasks.setAlignment(Pos.CENTER);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(selectByLabel, radioButtons, sprintSelector, startDatePicker, endDatePicker, selectSprintBtn, sprintDetails, cycleTime, tasks, scatterChart);
        Scene scene = new Scene(root, 1500, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cycle Time Chart Display " + sprint);
        primaryStage.show();

        selectSprintBtn.setOnAction(e -> {
            if (selectBySprintRadio.isSelected()) {
                String selectedSprint = sprintSelector.getValue();
                if (selectedSprint != null) {
                    getSprintData(selectedSprint);
                    displayChart();
                } else {
                    AlertPopup.showAlert("Error", "Please select a sprint.");
                }
            } else if (selectByDatesRadio.isSelected()) {
                LocalDate selectedStartDate = startDatePicker.getValue();
                LocalDate selectedEndDate = endDatePicker.getValue();
                if (selectedStartDate != null && selectedEndDate != null) {
                    // Check if start date is before end date
                    if (selectedStartDate.isAfter(selectedEndDate)) {
                        AlertPopup.showAlert("Error", "Start date must be before end date.");
                    } else {
                        firstDate = selectedStartDate.format(dateFormatter);
                        lastDate = selectedEndDate.format(dateFormatter);
                        displayChart();
                    }
                } else {
                    AlertPopup.showAlert("Error", "Please select both start and end dates.");
                }
            }
        });
    }
    private void displayChart() {
        scatterChart.getData().clear();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        getSprintData();
        int start_year = Integer.parseInt(firstDate.substring(0, 4));
        int last_year = Integer.parseInt(lastDate.substring(0, 4));
        int start_month = Integer.parseInt(firstDate.substring(5, 7));
        int last_month = Integer.parseInt(lastDate.substring(5, 7));
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
        Map<String, List<Pair<String, Integer>>> orderedCycleTime = CycleTime.getMatrixData(projectID, authToken, TAIGA_API_ENDPOINT, selectedSprint.get());

        computeCycleTime = 0.0f;
        numberTaskCompleted = 0;
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
        computeCycleTime /= numberTaskCompleted;
        valueCycleTime.setText(String.format("%.2f", computeCycleTime));
        valueTaskCompleted.setText(String.valueOf(numberTaskCompleted));
    }
    private void getSprintData(String selectedSprint) {
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        try {
            SprintData sprintDetails = SprintUtils.getSprintDetails(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);

            firstDate = sprintDetails.getStart_date();
            lastDate = sprintDetails.getEnd_date();
        } catch (Exception e) {
            AlertPopup.showAlert("Error", "Please try a done sprint.");
        }
    }
}
