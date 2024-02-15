import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class LeadTime {

    public static Map<String, Map<String, Object>> getLeadTimePerTask(int projectId, String authToken, String endpoint ) {
        List<JsonNode> closedTasks = Tasks.getClosedTasks(projectId, authToken, endpoint);
        Map<String, Map<String, Object>> LeadTimeMap = new HashMap<>();

        for (JsonNode task : closedTasks) {
            Map<String, Object> data = new HashMap<>();
            String taskId = task.path("id").asText();
            String taskName = task.path("subject").asText();
            LocalDateTime createdDate = parseDateTime(task.get("created_date").asText());
            LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());
            long leadTime = Duration.between(createdDate, finishedDate).toDays();

            data.put("Name",taskName);
            data.put("startDate",createdDate.toLocalDate());
            data.put("endDate",finishedDate.toLocalDate());
            data.put("leadTimeInDays",leadTime);

            String taskDetails = taskId + ": " + taskName;

            LeadTimeMap.put(taskDetails,data);
            System.out.println("task Name: " + taskName + " Lead Time: " + leadTime +" days\n");
        }
        return LeadTimeMap;
    }
    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

}
