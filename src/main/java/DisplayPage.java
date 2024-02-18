import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

import javafx.application.Platform;
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

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

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
        metricSelector.setItems(FXCollections.observableArrayList("BurnDown Chart", "Cycle Time", "Lead Time"));
        System.out.println("ComboBox Items: " + metricSelector.getItems());


        Button closeBtn = new Button("Submit");
        closeBtn.setOnAction(e -> {
            String slugURL = slugInput.getText();
            projectID = Project.getProjectId(authToken, GlobalData.getTaigaURL(), slugURL);
            String selectedOption = metricSelector.getValue();
            System.out.println(projectID);
            System.out.println(selectedOption + " Selected!!");
            switch (selectedOption) {
                case "Lead Time":
                    Map<String, Map<String, Object>> leadTimeMap = LeadTime.getLeadTimePerTask(projectID, authToken,
                            TAIGA_API_ENDPOINT);
                    LeadTimeGUI leadTimeGUI = new LeadTimeGUI(leadTimeMap);
                    leadTimeGUI.start(new Stage());
                    break;

				case "Cycle Time":
					          CycleTimeGUI ct = new CycleTimeGUI(projectID,authToken);
                    ct.start(new Stage());
                    break;
                case "BurnDown Chart":
                    List<JsonNode> sprintList = Burndown.getMilestoneList(authToken, TAIGA_API_ENDPOINT, projectID);
                    List<String> sprints = Burndown.getSprints();
                    Burndown stats = Burndown.getSprint(authToken, TAIGA_API_ENDPOINT, projectID, "Sprint 1");
                    BurndownGUI bd = new BurndownGUI(stats.getProgress());
                    bd.start(new Stage());
                    //createBurnDownChart(authToken, window, sprints);
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

    private static void createBurnDownChart(String authToken, Stage window, List<String> sprints) {
        window.hide();
        Stage window2 = new Stage();

        // locking events to other windows
        window2.initModality(Modality.APPLICATION_MODAL);
        window2.setTitle("Select Sprint: ");
        window2.setMinWidth(250);
        System.out.println("new window fn");

        // for(String sprint : sprints){
        // System.out.println(sprint);
        // }

        // Create a new window
        Label label2=new Label();
        label2.setText("List of Sprints");

        ComboBox<String> sprintSelector = new ComboBox<>();
        sprintSelector.setMinSize(100, 30); // Set minimum width and height

        sprintSelector.setPromptText("Select a sprint: ");
        // sprintSelector.setStyle("");
        System.out.println(sprintSelector.isVisible());

        // Populate the Combo Box with the sprints
        sprintSelector.setItems(FXCollections.observableArrayList(sprints));
        System.out.println("ComboBox Items: " + sprintSelector.getItems());
        System.out.println("Populated sprint");

        Button subBtn = new Button("Submit");
        subBtn.setOnAction(e -> {
            String selectedSprint = sprintSelector.getValue();
            System.out.println(projectID);
            System.out.println(selectedSprint + " Selected!!");

            Burndown stats = Burndown.getSprint(authToken, TAIGA_API_ENDPOINT, projectID, selectedSprint);
            BurndownGUI bd = new BurndownGUI(stats.getProgress());
            bd.start(new Stage());
        });

        // Platform.runLater(() -> {
        // sprintSelector.setItems(FXCollections.observableArrayList(sprints));
        // });

        VBox layout2 = new VBox(10);
        layout2.getChildren().addAll(label2,sprintSelector, subBtn);
        layout2.setAlignment(Pos.CENTER);
        layout2.setPadding(new Insets(20, 20, 20, 20));
        Scene scene2 = new Scene(layout2, 500, 300);
        window2.setScene(scene2);
        window2.showAndWait();
    }
}
