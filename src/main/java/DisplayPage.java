import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DisplayPage {

    public static DisplayPage.SlugURLHandler SlugURLHandler;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    //An interface for callback when the slugURL is submitted
    public interface SlugURLHandler{
        void handle(String slugURL);
    }
    public static int projectID;
    public static void display(String authToken, SlugURLHandler handler){
        Stage window=new Stage();

        //Blocking events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter Slug URL: ");
        window.setMinWidth(250);

        Label label=new Label();
        label.setText("Enter Slug URL Below: ");

        //Input Field for slug URL
        TextField slugInput=new TextField();

        //Dropdown menu to select the metric
        ComboBox<String> metricSelector=new ComboBox<>();
        metricSelector.setPromptText("Select a metric: ");

        //Populate the Combo Box with the Metrics
        metricSelector.setItems(FXCollections.observableArrayList("BurnDown Chart","Cycle Time", "Lead Time"));

        //Input Field for Sprint Input
        TextField sprintInput = new TextField("Enter Sprint");
        sprintInput.setPrefWidth(80);
        sprintInput.setMaxWidth(80);

        Button closeBtn=new Button("Submit");
        closeBtn.setOnAction(e->{
            String slugURL= slugInput.getText();
            projectID= Project.getProjectId(authToken,GlobalData.getTaigaURL(),slugURL);
            String selectedOption=metricSelector.getValue();
            System.out.println(projectID);
            System.out.println(selectedOption+" Selected!!");

            //Get First and Last Date of Sprint
            String sprint = sprintInput.getText();
            String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
            List<JsonNode> sprintDetails = Burndown.getMilestoneStats(authToken, TAIGA_API_ENDPOINT, projectID, sprint);

            String firstDate = sprintDetails.get(1).asText();
            String lastDate = sprintDetails.get(2).asText();

            if(Objects.equals(selectedOption, "Cycle Time")){
                try {
                    CycleTimeGUI ct = new CycleTimeGUI(projectID,authToken,TAIGA_API_ENDPOINT,firstDate,lastDate);
                    ct.start(new Stage());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        VBox layout=new VBox(10);
        layout.getChildren().addAll(label,slugInput,metricSelector,sprintInput,closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene=new Scene(layout,500,300);
        window.setScene(scene);
        window.showAndWait();
    }
}
