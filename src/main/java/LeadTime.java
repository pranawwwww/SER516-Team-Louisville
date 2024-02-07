import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class LeadTime {

    public static Map<String, Long> getLeadTimePerTask(int projectId, String authToken, String endpoint ) {
        List<JsonNode> closedTasks = Tasks.getClosedTasks(projectId, authToken, endpoint);
        Map<String, Long> LeadTimeMap = new HashMap<>();

        for (JsonNode task : closedTasks) {
            String taskId = task.get("id").asText();
            LocalDateTime createdDate = parseDateTime(task.get("created_date").asText());
            LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());
            long leadTime = Duration.between(createdDate, finishedDate).toDays();

            LeadTimeMap.put(taskId,leadTime);

        }
        return LeadTimeMap;
    }
    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

}
