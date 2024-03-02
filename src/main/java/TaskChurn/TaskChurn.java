package TaskChurn;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.JsonNode;

import utils.SprintData;
import utils.SprintUtils;
import utils.Tasks;

public class TaskChurn {

    private static List<JsonNode> sprintTaskHistories(int projectId, String authToken, String endpoint, String sprint ) {
        List<JsonNode> allTaskHistory = new ArrayList<>();
        //fetch all tasks and their history
        List<JsonNode> allTasks = Tasks.getAllTasks(projectId,authToken,endpoint,sprint);
        for(JsonNode task:allTasks){
            String id = task.path("id").asText();
            JsonNode taskHistory = Tasks.getIndividualTaskHistory(projectId,authToken,endpoint, id);
            allTaskHistory.add(taskHistory);
        }
        return allTaskHistory;
    }

    public static TreeMap<LocalDate,Float> calculateTaskChurn (int projectId, String authToken, String endpoint, String sprint) {
        // find start date of sprint 
        TreeMap<LocalDate,Integer> taskCount = new TreeMap<>();
        TreeMap<LocalDate,Integer> taskModifiedCount = new TreeMap<>();
        TreeMap<LocalDate,Float> taskChurn = new TreeMap<>();
        
        try{
            SprintData sprintDetails = SprintUtils.getSprintDetails(authToken, endpoint, projectId, sprint);
            if(sprintDetails == null){
                throw new IllegalArgumentException("Sprint has not started");   
            }
        
        List<JsonNode> taskList = sprintTaskHistories(projectId, authToken, endpoint, sprint); 
        List<JsonNode> allTasks = Tasks.getAllTasks(projectId, authToken, endpoint,sprint);
        //find number of tasks per day in sprint

        for(JsonNode task: allTasks){
            //populate task count
            LocalDate createdDate = parseDateTime(task.path("created_date").asText()).toLocalDate();
            if(taskCount.containsKey(createdDate)){
                Integer temp = taskCount.get(createdDate);
                taskCount.put(createdDate,++temp);
            }
            else
                taskCount.put(createdDate,1);
        }
        Integer baseline = taskCount.get(taskCount.firstKey());
        Integer dayOne = baseline;
        
        for(JsonNode task: taskList){
            if(task.isArray()){
                for(JsonNode node: task){
                    LocalDate createdAt = parseDateTime(node.path("created_at").asText()).toLocalDate();
                    if(!node.path("diff").has("status")){
                        if(taskModifiedCount.containsKey(createdAt)){
                            Integer temp = taskModifiedCount.get(createdAt);
                            taskModifiedCount.put(createdAt,++temp);
                        }
                        else
                        taskModifiedCount.put(createdAt,1);
                    }
                    
                }
            }
        }
        taskModifiedCount.put(taskCount.firstKey(),0);
        TreeSet<LocalDate> allKeys = new TreeSet<>();
        allKeys.addAll(taskCount.keySet());
        allKeys.addAll(taskModifiedCount.keySet());
        for (LocalDate date : allKeys) {

            Float churn = (((float)(Math.abs(taskCount.getOrDefault(date,0) - baseline) + taskModifiedCount.getOrDefault(date,0))/(float)dayOne)*100);
            if(taskCount.get(date) != null){
                baseline = taskCount.get(date);
            }
            taskChurn.put(date,Math.abs(churn));
        }

        } catch (Exception e){
            e.printStackTrace();
        }
        return taskChurn;
        
    }
    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTimeString, formatter);
    }

}
