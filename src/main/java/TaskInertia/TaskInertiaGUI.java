package TaskInertia;

import java.time.LocalDate;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.Project;

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
        LocalDate projectStartDate = Project.getProjectStartDate(authToken, endpoint, projectId);
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(projectStartDate); 
        startDatePicker.setDayCellFactory(getDateCellFactory(projectStartDate, LocalDate.now()));
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(LocalDate.now()); 
        endDatePicker.setDayCellFactory(getDateCellFactory(projectStartDate, LocalDate.now()));

        // Create a button to print selected dates
        Button btnSubmit = new Button("Submit");
        btnSubmit.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
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


        private Callback<DatePicker, DateCell> getDateCellFactory(LocalDate minDate, LocalDate maxDate) {
        return new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Disable dates before minDate or after maxDate
                        if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Optional: Apply a style (e.g., background color)
                        }
                    }
                };
            }
        };
    }
    
}
