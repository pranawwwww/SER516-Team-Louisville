package TaskDefectDensity;

import com.fasterxml.jackson.databind.JsonNode;

import utils.Tasks;

import java.util.List;
import java.util.ArrayList;

public class TaskDefectDensity{

    private List<JsonNode> unfinishedTasks = new ArrayList<>();
    private List<JsonNode> totalTasks = new ArrayList<>();
    public List<JsonNode> getUnfinishedTasks() {
        return unfinishedTasks;
    }

    public List<JsonNode> getTotalTasks() {
        return totalTasks;
    }

    TaskDefectDensity(List<JsonNode> totalTasks, List<JsonNode> unfinishedTasks, String authToken, String TAIGA_API_ENDPOINT, int projectId, String sprint ){
        this.totalTasks = Tasks.getAllTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);
        this.unfinishedTasks = Tasks.getUnfinishedTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);
    }
}