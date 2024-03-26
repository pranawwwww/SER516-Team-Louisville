package TaskInertia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;

import utils.Tasks;

public class TaskInertia {
    public static TreeMap<LocalDate, Float> getTaskInertia(int projectId, String authToken, String endpoint, LocalDate startDate, LocalDate endDate){
        TreeMap<LocalDate, Float> taskInertia = new TreeMap<>();
        TreeMap<LocalDate, Integer> taskCount = new TreeMap<>();
        List<JsonNode> taskHistory = Tasks.getProjectTaskHistory(projectId, authToken, endpoint);
        List<JsonNode> totalTaskList = Tasks.getAllTasksInProject(projectId, authToken, endpoint);
        for(JsonNode jsonNode: taskHistory){
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    JsonNode valuesDiff = node.get("values_diff");
                    if (valuesDiff != null && valuesDiff.has("status")) {
                        String createdAt = node.get("created_at").asText();
                        LocalDate createdDate = parseDateTime(createdAt).toLocalDate();
                        if(taskCount.containsKey(createdDate)){
                            int count = taskCount.get(createdDate)+1;
                            taskCount.put(createdDate, count);
                        }
                        else{
                            taskCount.put(createdDate, 1);
                        }
                    }
                }
            }
        }
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int totalTasks = totalTasksAtGivenDate(date, totalTaskList);
            if(!taskCount.containsKey(date)){
                taskInertia.put(date, (float)totalTasks);
            }
            else{
                float temp = Math.abs((float)(totalTasks - taskCount.get(date))/(float)totalTasks*100);
                taskInertia.put(date, temp);
            }

        }

        return taskInertia;
    }

    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    private static int totalTasksAtGivenDate(LocalDate date, List<JsonNode> tasks){
        int total=0;
        for (JsonNode task: tasks){
            String finishedAt = task.get("finished_date").asText();
            LocalDate createdDate = parseDateTime(task.get("created_date").asText()).toLocalDate();
           
            if(finishedAt == "null"){
                if(createdDate.isBefore(date) || createdDate.isEqual(date)){
                    total+=1;
                } 
            }
            else{
                LocalDate finishedDate = parseDateTime(finishedAt).toLocalDate();
                if((createdDate.isBefore(date) || createdDate.isEqual(date)) && (finishedDate.isAfter(date) || finishedDate.equals(date))){
                    total+=1;
                }  
            }
        }
        return total;
    }
}
