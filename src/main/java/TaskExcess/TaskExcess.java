package TaskExcess;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utils.SprintData;
import utils.SprintUtils;
import utils.Tasks;

import java.util.ArrayList;
import java.util.List;

public class TaskExcess {
    private static final StringProperty selectedSprint = new SimpleStringProperty();
    private static DoubleProperty taskExcess = new SimpleDoubleProperty(0.0);
    private List<JsonNode> newTasks = new ArrayList<>();
    private List<JsonNode> totalTasks = new ArrayList<>();
    private boolean validSprint = true;

    public double getNewTasks() {
        return this.newTasks.size();
    }
    public int getNumberOfTotalTasks(){
        return this.totalTasks.size();
    }
    public boolean getValidSprint(){
        return this.validSprint;
    }
    public static void setSelectedSprint(String value) {
        selectedSprint.set(value);
    }
    public static String getSelectedSprint() {
        return selectedSprint.get();
    }
    public static StringProperty selectedSprintProperty() {
        return selectedSprint;
    }

    public static double getTaskExcess() {
        return taskExcess.get();
    }

    public static DoubleProperty taskExcessProperty() {
        return taskExcess;
    }
    public void setTaskExcess(double taskExcess) {
        this.taskExcess.set(taskExcess);
    }
    public TaskExcess(String authToken, String TAIGA_API_ENDPOINT, int projectId, String sprint ){
        try{
            SprintData sprintDetails = SprintUtils.getSprintDetails(authToken, TAIGA_API_ENDPOINT, projectId, sprint);
            if(sprintDetails == null){
                this.validSprint = false;
                throw new IllegalArgumentException("Sprint has not started");
            }
            this.totalTasks = Tasks.getAllTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);
            this.newTasks = Tasks.getNewTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);
            this.taskExcess = new SimpleDoubleProperty((double)(this.getNewTasks()/this.getNumberOfTotalTasks()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
