package LeadTime;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import utils.SprintData;
import utils.SprintUtils;
import utils.Tasks;

public class LeadTime {
    
    static Map<String, Map<String, Object>> LeadTimeMap = new HashMap<>();

    public static Map<String, Map<String, Object>> getLeadTimePerTask(int projectId, String authToken, String endpoint, String sprint ) {
        try{
            SprintData sprintDetails = SprintUtils.getSprintDetails(authToken, endpoint, projectId, sprint);
            if(sprintDetails == null){
                throw new IllegalArgumentException("Sprint has not started");   
            }
            if(!LeadTimeMap.isEmpty())
                LeadTimeMap.clear();
            String firstDate = sprintDetails.getStart_date();
            String lastDate = sprintDetails.getEnd_date();
            int start_year = Integer.parseInt(firstDate.substring(0,4));
            int last_year = Integer.parseInt(lastDate.substring(0,4));
            int start_month = Integer.parseInt(firstDate.substring(5,7));
            int last_month = Integer.parseInt(lastDate.substring(5,7));
            int start_date = Integer.parseInt(firstDate.substring(8));
            int last_date = Integer.parseInt(lastDate.substring(8));
            LocalDateTime startDate = LocalDate.of(start_year, start_month, start_date).atTime(00, 00, 00);
            LocalDateTime endDate = LocalDate.of(last_year, last_month, last_date).atTime(00, 00, 00);
            List<JsonNode> closedTasks = Tasks.getClosedTasks(projectId, authToken, endpoint);
    
            for (JsonNode task : closedTasks) {
                Map<String, Object> data = new HashMap<>();
                LocalDateTime createdDate = parseDateTime(task.get("created_date").asText());
                LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());
                if(createdDate.isBefore(endDate) && finishedDate.isAfter(startDate) ) {
                    String taskId = task.path("id").asText();
                    String taskName = task.path("subject").asText();
                    String userStoryName = task.path("user_story_extra_info").path("subject").asText();
                    JsonNode userStoryExtraInfo = task.path("user_story_extra_info");
                    JsonNode epicsArray = userStoryExtraInfo.path("epics");
                    String epicName = "";
                    if (epicsArray.isArray() && epicsArray.size() > 0) {
                        JsonNode firstEpic = epicsArray.get(0);
                        if (firstEpic != null) {
                            epicName = firstEpic.path("subject").asText();
                        }
                    }
                    
                    
                    long leadTime = Duration.between(createdDate, finishedDate).toDays();
                    data.put("taskName",taskName);
                    data.put("userStoryName",userStoryName);
                    data.put("epicName",epicName);
                    data.put("startDate",createdDate.toLocalDate());
                    data.put("endDate",finishedDate.toLocalDate());
                    data.put("leadTimeInDays",leadTime);
        
                    String taskDetails = taskId + ": " + taskName;
        
                    LeadTimeMap.put(taskDetails,data);
                    System.out.println("task Name: " + taskName + " Lead Time: " + leadTime +" days\n");
                }
             }
         }   
        
        catch (Exception e) {
            System.out.println("empty sprint selected");
            LeadTimeMap.clear();
        }
        return LeadTimeMap;
    }
    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

}
