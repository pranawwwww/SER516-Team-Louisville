import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Map;
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

private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();

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

        Button closeBtn=new Button("Submit");
        closeBtn.setOnAction(e->{
            String slugURL= slugInput.getText();
            projectID= Project.getProjectId(authToken,GlobalData.getTaigaURL(),slugURL);
            String selectedOption=metricSelector.getValue();
            System.out.println(projectID);
            System.out.println(selectedOption+" Selected!!");
			switch (selectedOption) {
                case "Lead Time":
                    Map<String, Map<String, Object>> leadTimeMap = LeadTime.getLeadTimePerTask(projectID, authToken, TAIGA_API_ENDPOINT );
                    LeadTimeGUI leadTimeGUI = new LeadTimeGUI(leadTimeMap);
                    leadTimeGUI.start(new Stage());
                    break;
				case "Cycle Time":
					CycleTimeGUI ct = new CycleTimeGUI(projectID,authToken,"ENDPOINT","SPRINT_FIRST_DATE","SPRINT_LAST_DATE");
                    ct.start(new Stage());
                default:
                    break;
            }        });
        VBox layout=new VBox(10);
        layout.getChildren().addAll(label,slugInput,metricSelector,closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene=new Scene(layout,500,300);
        window.setScene(scene);
        window.showAndWait();
    }
}
