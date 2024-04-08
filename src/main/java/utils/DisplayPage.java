package utils;
import BurnDown.BurndownGUI;
import CycleTime.CycleTimeGUI;
import LeadTime.LeadTimeGUI;
import TaskChurn.TaskChurnGUI;
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
            switch (selectedOption) {
                case "Lead Time":
                    LeadTimeGUI leadTimeGUI = new LeadTimeGUI(projectID,authToken,TAIGA_API_ENDPOINT,slugURL);
                    leadTimeGUI.start(new Stage());
                    break;
				case "Cycle Time":
					CycleTimeGUI ct = new CycleTimeGUI(projectID,authToken,slugURL);
                    ct.start(new Stage());
                    break;
                case "BurnDown Chart":
                    BurndownGUI bd = new BurndownGUI(projectID,authToken,slugURL);
                    bd.start(new Stage());
                    break;
                case "Task Defect Density":
                    TaskDefectDensityGUI tdd = new TaskDefectDensityGUI(projectID, authToken, slugURL);
                    tdd.start(new Stage());
                    break;
                case "Task Churn":
                    TaskChurnGUI tc = new TaskChurnGUI(authToken, TAIGA_API_ENDPOINT, projectID, slugURL);
                    tc.start(new Stage());
                    break;
                case "Task Excess":
                    TaskExcessGUI taskExcess = new TaskExcessGUI(authToken, TAIGA_API_ENDPOINT, projectID, slugURL);
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
        layout.getChildren().addAll(label, slugInput, metricSelector, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(layout, 500, 300);
        window.setScene(scene);
        window.showAndWait();
    }
}
