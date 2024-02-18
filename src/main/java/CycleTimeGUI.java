import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.util.List;

public class CycleTimeGUI extends Application {

    private int projectID;
    private String authToken;
    private String TAIGA_API_ENDPOINT;
    private String firstDate;
    private String lastDate;

    public CycleTimeGUI(int projectID,String authToken, String TAIGA_API_ENDPOINT,String firstDate, String lastDate) {
        this.projectID = projectID;
        this.authToken = authToken;
        this.TAIGA_API_ENDPOINT = TAIGA_API_ENDPOINT;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
        System.out.println(projectID+"\n"+authToken+"\n"+TAIGA_API_ENDPOINT+"\n"+firstDate+"\n"+lastDate);
//        return;
    }



    public static void main(String[] args) {

//        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage window=new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Cycle Time Chart");
        window.setMinWidth(300);

        TextField sprintInput = new TextField("Enter Sprint");
        sprintInput.setPrefWidth(100);
        sprintInput.setMaxWidth(100);

        Button submitButton=new Button("Submit");

        submitButton.setOnAction(e->{
            String sprint = sprintInput.getText();
            String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
            Burndown sprintDetails = Burndown.getSprint(authToken, TAIGA_API_ENDPOINT, projectID, sprint);

            String firstDate = sprintDetails.getStart_date();
            String lastDate = sprintDetails.getEnd_date();

            new CycleTimeGUI(projectID,authToken,TAIGA_API_ENDPOINT,firstDate,lastDate);
        });
        VBox layout=new VBox(10);
        layout.getChildren().addAll(sprintInput,submitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene=new Scene(layout,500,300);
        window.setScene(scene);
        window.showAndWait();
    }
}
