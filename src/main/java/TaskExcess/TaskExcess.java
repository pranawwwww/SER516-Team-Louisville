package TaskExcess;

import com.fasterxml.jackson.databind.JsonNode;
import utils.SprintData;
import utils.SprintUtils;
import utils.Tasks;

import java.util.ArrayList;
import java.util.List;

public class TaskExcess {
    private List<JsonNode> newTasks = new ArrayList<>();
    private List<JsonNode> totalTasks = new ArrayList<>();
    private boolean validSprint = true;
    private double taskExcess;
    public double getTaskExcess() {
        return this.taskExcess;
    }
    public double getNewTasks() {
        return this.newTasks.size();
    }
    public int getNumberOfTotalTasks(){
        return this.totalTasks.size();
    }
    public boolean getValidSprint(){
        return this.validSprint;
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
            this.taskExcess = (double)(this.getNewTasks()/this.getNumberOfTotalTasks());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
