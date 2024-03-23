package TaskInertia;

import java.time.LocalDate;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskInertiaGUI extends Application{

    private int projectId = 0;
    private String authToken = null;
    private String endpoint =null;



    public TaskInertiaGUI(int projectId, String authToken, String endpoint){
        this.projectId = projectId;
        this.authToken = authToken;
        this.endpoint = endpoint;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Inertia");
        // Create DatePickers for startDate and endDate
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(LocalDate.now()); // Default to current date
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now().plusDays(1)); // Default to the next day

        //TODO: Remove prints
        // Create a button to print selected dates
        Button btnSubmit = new Button("Submit");
        btnSubmit.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            TreeMap<LocalDate, Float> taskInertiaMap = TaskInertia.getTaskInertia(projectId, authToken, endpoint, startDate, endDate);
        });

        // Layout
        VBox vBox = new VBox(10, startDatePicker, endDatePicker, btnSubmit);
        vBox.setAlignment(Pos.TOP_CENTER); 
        vBox.setPadding(new Insets(20, 0, 0, 0));

        // Scene
        Scene scene = new Scene(vBox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
