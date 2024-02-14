import javafx.application.Application;
import javafx.stage.Stage;

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

    }
}
