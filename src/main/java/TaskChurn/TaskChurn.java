package TaskChurn;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import utils.Tasks;
import utils.UserStoryUtils;

public class TaskChurn {

    private List<JsonNode> sprintUserStoryHistories(int projectId, String authToken, String endpoint, String sprint ) {
        //fetch all user stories and their history
        List<JsonNode> allUserStoryHistory = new ArrayList<>();
        List<JsonNode> allUserStories = UserStoryUtils.getAllUserStories(authToken, endpoint, projectId, sprint);
        for(JsonNode userstory :allUserStories){
            String id = userstory.path("id").asText();
            JsonNode USHistory = UserStoryUtils.getUserStoryHistory(projectId,authToken,endpoint, id);
            allUserStoryHistory.add(USHistory);
        }
    }

    private List<JsonNode> sprintTaskHistories(int projectId, String authToken, String endpoint, String sprint ) {
        List<JsonNode> allTaskHistory = new ArrayList<>();
        //fetch all tasks and their history
        List<JsonNode> allTasks = Tasks.getAllTasks(projectId,authToken,endpoint,sprint);
        for(JsonNode task:allTasks){
            String id = task.path("id").asText();
            JsonNode taskHistory = Tasks.getIndividualTaskHistory(projectId,authToken,endpoint, id);
            allTaskHistory.add(taskHistory);
        }
    }
    private List<JsonNode> sprintIssueHistories(int projectId, String authToken, String endpoint, String sprint ) {
        List<JsonNode> allIssuesHistory = new ArrayList<>();
        //fetch all tasks and their history
        List<JsonNode> allIssues = UserStoryUtils.getAllIssues(authToken,endpoint,projectId,sprint);
        for(JsonNode issue:allIssues){
            String id = issue.path("id").asText();
            JsonNode issueHistory = UserStoryUtils.getIssueHistory(projectId,authToken,endpoint, id);
            allIssues.add(issueHistory);
        }
    }

    private Map<Date,Integer> calculateTaskChurn (int projectId, String authToken, String endpoint, String sprint) {
        // placeholder to calculate task churn from provided data.
    }
}
