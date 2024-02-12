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
        metricSelector.setItems(FXCollections.observableArrayList("BurnDown Chart","Other Metric"));

        Button closeBtn=new Button("Submit");
        closeBtn.setOnAction(e->{
            String slugURL= slugInput.getText();
            projectID= Project.getProjectId(authToken,GlobalData.getTaigaURL(),slugURL);
            String selectedOption=metricSelector.getValue();
            System.out.println(projectID);
            System.out.println(selectedOption+" Selected!!");
        });
        VBox layout=new VBox(10);
        layout.getChildren().addAll(label,slugInput,metricSelector,closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene=new Scene(layout,400,250);
        window.setScene(scene);
        window.showAndWait();
    }
}
