package utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import BurnDown.BurnDownDataPoint;
import BurnDown.Burndown;
import BurnDown.BurndownGUI;
import CycleTime.CycleTimeGUI;
import LeadTime.LeadTime;
import LeadTime.LeadTimeGUI;
import TaskChurn.TaskChurn;
import TaskChurn.TaskChurnGUI;
import TaskDefectDensity.TaskDefectDensity;
import TaskDefectDensity.TaskDefectDensityGUI;
import TaskExcess.TaskExcessGUI;
import TaskInertia.TaskInertiaGUI;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DisplayPage {

    public static DisplayPage.SlugURLHandler SlugURLHandler;

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();

    // An interface for callback when the slugURL is submitted
    public interface SlugURLHandler {
        void handle(String slugURL);
    }

    public static int projectID;

    public static void display(String authToken, SlugURLHandler handler) {
        Stage window = new Stage();

        // Blocking events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter Slug URL: ");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText("Enter Slug URL Below: ");

        // Input Field for slug URL
        TextField slugInput = new TextField();

        // Dropdown menu to select the sprint
        ComboBox<String> sprintSelector = new ComboBox<>();    
        sprintSelector.setPromptText("select a sprint");
        sprintSelector.getSelectionModel().clearSelection();
        sprintSelector.setValue(null);

        // Button to trigger sprint selection
        Button selectSprintBtn = new Button("Fetch all Sprints");  
        selectSprintBtn.setOnAction(e -> {
            // Clearing the ComboBox
            sprintSelector.getItems().removeAll(sprintSelector.getItems());
            // Additional clearing methods if necessary
            sprintSelector.getSelectionModel().clearSelection();
            sprintSelector.setValue("");    
            System.out.println("ComboBox Items: " + sprintSelector.getItems());
            sprintSelector.setPromptText("select a sprint");
            selectSprint(authToken, slugInput.getText(), sprintSelector);});  
        //selectSprintBtn.setOnAction(e -> selectSprint(authToken, slugInput.getText(), sprintSelector));
        

        // Dropdown menu to select the metric
        ComboBox<String> metricSelector = new ComboBox<>();
        metricSelector.setPromptText("Select a metric: ");
        
        // Populate the Combo Box with the Metrics
        metricSelector.setItems(FXCollections.observableArrayList("BurnDown Chart", "Cycle Time", "Lead Time","Task Defect Density","Task Churn","Task Excess","Task Inertia"));
        System.out.println("ComboBox Items: " + metricSelector.getItems());


        Button closeBtn = new Button("Submit");
        closeBtn.setOnAction(e -> {
            String slugURL = slugInput.getText();
            projectID = Project.getProjectId(authToken, GlobalData.getTaigaURL(), slugURL);
            String selectedOption = metricSelector.getValue();
            String selectedSprint = sprintSelector.getValue();
            System.out.println(selectedSprint);
            System.out.println(projectID);
            System.out.println(selectedOption + " Selected!!");
            switch (selectedOption) {
                case "Lead Time":
                    Map<String, Map<String, Object>> leadTimeMap = LeadTime.getLeadTimePerTask(projectID, authToken,
                            TAIGA_API_ENDPOINT,selectedSprint);
                    LeadTimeGUI leadTimeGUI = new LeadTimeGUI(leadTimeMap,selectedSprint);
                    leadTimeGUI.start(new Stage());
                    break;
				case "Cycle Time":
					CycleTimeGUI ct = new CycleTimeGUI(projectID,authToken,selectedSprint);
                    ct.start(new Stage());
                    break;
                case "BurnDown Chart":
                    SprintData stats = SprintUtils.getSprintDetails(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);
                    List<BurnDownDataPoint> progress = Burndown.getBurnDownProgress(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);
                    System.out.println(progress);
                    BurndownGUI bd = new BurndownGUI(stats,progress,selectedSprint);
                    bd.start(new Stage());
                    break;
                case "Task Defect Density":
                    TaskDefectDensity taskDefectDensity=new TaskDefectDensity(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);
                    TaskDefectDensityGUI tdd = new TaskDefectDensityGUI(taskDefectDensity.getNumberOfDeletedTasks(), taskDefectDensity.getNumberOfUnfinishedTasks(), taskDefectDensity.getNumberOfTotalTasks(), taskDefectDensity.getTaskDefectDensity(), taskDefectDensity.getValidSprint());
                    tdd.start(new Stage());
                    break;
                case "Task Churn":
                    TaskChurnGUI tc = new TaskChurnGUI(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);
                    tc.start(new Stage());
                    break;
                case "Task Excess":
                    TaskExcessGUI taskExcess = new TaskExcessGUI(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);
                    taskExcess.start(new Stage());
                    break;
                case "Task Inertia":
                    TaskInertiaGUI taskInertia = new TaskInertiaGUI(projectID, authToken, TAIGA_API_ENDPOINT);
                    taskInertia.start(new Stage());
                    break;
                default:
                    break;
            }

        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, slugInput,selectSprintBtn, sprintSelector, metricSelector, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(layout, 500, 300);
        window.setScene(scene);
        window.showAndWait();
    }

    private static void selectSprint(String authToken, String slugURL, ComboBox<String> sprintSelector) {
        try{
            projectID = Project.getProjectId(authToken, GlobalData.getTaigaURL(), slugURL);
            if(projectID == -1){
                throw new NoSuchElementException();
            }
            SprintUtils.getMilestoneList(authToken, TAIGA_API_ENDPOINT, projectID);
            List<String> sprints = new ArrayList<>();
            sprints= SprintUtils.getSprints();
            
            sprintSelector.getSelectionModel().clearSelection();
            sprintSelector.setValue(null);
            sprintSelector.setPromptText("select a sprint");
            sprintSelector.setItems(FXCollections.observableArrayList(sprints));
            sprintSelector.layout();
        } catch (NoSuchElementException exception){
            AlertPopup.showAlert("Error", "Please Try a Sprint which has been started.");
        }

    }
}
