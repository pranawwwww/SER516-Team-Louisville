package utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Tasks {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static List<JsonNode> getClosedTasks(int projectId, String authToken, String TAIGA_API_ENDPOINT,String sprint) {


        // API to get list of all tasks in a project.
        int milestoneId = SprintUtils.getSprintIdByName(authToken,TAIGA_API_ENDPOINT,projectId,sprint);
        String endpoint = TAIGA_API_ENDPOINT + "/tasks?milestone="+milestoneId;
        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            JsonNode tasksNode = objectMapper.readTree(responseJson);
            List<JsonNode> closedTasks = new ArrayList<>();

            for (JsonNode taskNode : tasksNode) {
                boolean isClosed = taskNode.has("is_closed") && taskNode.get("is_closed").asBoolean();
                if (isClosed) {
                    closedTasks.add(taskNode);
                }
            }

            return closedTasks;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static int[] calculateCycleTime(JsonNode historyData, LocalDateTime finishedDate) {
        int cycleTime = 0;
        int closedTasks = 0;

        for (JsonNode event : historyData) {
            JsonNode valuesDiff = event.get("values_diff");
            if (valuesDiff != null && valuesDiff.has("status")) {
                JsonNode statusDiff = valuesDiff.get("status");
                if (statusDiff.isArray() && statusDiff.size() == 2
                        && "New".equals(statusDiff.get(0).asText()) && "In progress".equals(statusDiff.get(1).asText())) {
                    LocalDateTime createdAt =parseDateTime(event.get("created_at").asText());
                    cycleTime += Duration.between(createdAt.toLocalDate().atStartOfDay(), finishedDate.toLocalDate().atStartOfDay()).toDays();
                    closedTasks++;
                }
            }
        }

        return new int[]{cycleTime, closedTasks};
    }

    public static List<Integer> getTaskHistory(List<JsonNode> tasks, String authToken, String TAIGA_API_ENDPOINT) {
        List<Integer> result = new ArrayList<>(List.of(0, 0));


        for (JsonNode task : tasks) {
            int taskId = task.get("id").asInt();

            // API to get history of task
            String taskHistoryUrl = TAIGA_API_ENDPOINT + "/history/task/" + taskId;

            try {
                HttpGet request = new HttpGet(taskHistoryUrl);
                request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                String responseJson = HTTPRequest.sendHttpRequest(request);

                JsonNode historyData = objectMapper.readTree(responseJson);
                LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());

                int[] cycleTimeAndClosedTasks = calculateCycleTime(historyData, finishedDate);
                result.set(0, result.get(0) + cycleTimeAndClosedTasks[0]);
                result.set(1, result.get(1) + cycleTimeAndClosedTasks[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    public static List<JsonNode> getAllTasks(int projectId, String authToken, String TAIGA_API_ENDPOINT,String sprint){

        int milestoneId = SprintUtils.getSprintIdByName(authToken,TAIGA_API_ENDPOINT,projectId,sprint);
        String endpoint = TAIGA_API_ENDPOINT + "/tasks?milestone="+milestoneId;
        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            JsonNode tasksNode = objectMapper.readTree(responseJson);
            List<JsonNode> allTasks = new ArrayList<>();
            for (JsonNode taskNode : tasksNode) {
                allTasks.add(taskNode);
            }
            return allTasks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public static List<JsonNode> getUnfinishedTasks(int projectId, String authToken, String TAIGA_API_ENDPOINT, String sprint) {
        int milestoneId = SprintUtils.getSprintIdByName(authToken, TAIGA_API_ENDPOINT, projectId, sprint);
        String endpoint = TAIGA_API_ENDPOINT + "/tasks?milestone=" + milestoneId;
        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            JsonNode tasksNode = objectMapper.readTree(responseJson);
            List<JsonNode> unfinishedTasks = new ArrayList<>();
            for (JsonNode taskNode : tasksNode) {
                JsonNode isClosedNode = taskNode.get("is_closed");
                if (isClosedNode != null && isClosedNode.isBoolean() && !isClosedNode.asBoolean()) {
                    unfinishedTasks.add(taskNode);
                }
            }
            return unfinishedTasks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<JsonNode> getTaskStatuses(int projectId, String authToken, String TAIGA_API_ENDPOINT, String sprint){
        List<JsonNode> result = new ArrayList<>();
        List<JsonNode> tasks=getAllTasks(projectId,authToken,TAIGA_API_ENDPOINT,sprint);

        for (JsonNode task : tasks) {
            int taskId = task.get("id").asInt();
            // API to get history of task
            String taskStatusUrl = TAIGA_API_ENDPOINT + "/task-statuses/" + taskId;

            try {
                HttpGet request = new HttpGet(taskStatusUrl);
                request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                String responseJson = HTTPRequest.sendHttpRequest(request);

                JsonNode historyData = objectMapper.readTree(responseJson);
                result.add(historyData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static JsonNode getIndividualTaskHistory(int projectId, String authToken, String TAIGA_API_ENDPOINT, String taskId){

        JsonNode taskHistory = null;
        String endpoint = TAIGA_API_ENDPOINT + "/history/task/" + taskId;
        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            taskHistory = objectMapper.readTree(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskHistory;
    }
    public static List<JsonNode> getTasksByCreatedDate(int projectId, String authToken, String TAIGA_API_ENDPOINT, String sprint, String date){
        List<JsonNode> tasks = getAllTasks(projectId,authToken,TAIGA_API_ENDPOINT,sprint);
        List<JsonNode> result = new ArrayList<>();
        for(JsonNode node: tasks){
            if(node.get("created_date").asText().substring(0,10).equals(date)){
                result.add(node);
            }
        }

        return result;
    }

    public static int getDeletedTasks(int projectId, String authToken, String TAIGA_API_ENDPOINT, String sprint){
        SprintData sd = SprintUtils.getSprintDetails(authToken,TAIGA_API_ENDPOINT,projectId,sprint);
        JsonNode dates = sd.getProgressNode();
        int deletedTasks = 0, totalTasks = 0;
        List<JsonNode> datesList = new ArrayList<>();
        if (dates.isArray()) {
            for (JsonNode jsonNode : dates) {
                datesList.add(jsonNode);
            }
        }
        if(datesList!=null){
            for(int i=0;i<datesList.size();i++){
                String date=datesList.get(i).get("day").asText();
                totalTasks+=getTasksByCreatedDate(projectId,authToken,TAIGA_API_ENDPOINT,sprint,date).size();
            }
        }
        deletedTasks = Math.abs(getAllTasks(projectId,authToken,TAIGA_API_ENDPOINT,sprint).size() - totalTasks);
        return deletedTasks;
    }

    public static List<JsonNode> getNewTasks(int projectId, String authToken, String TAIGA_API_ENDPOINT, String sprint){
        List<JsonNode> allTasks = getAllTasks(projectId,authToken,TAIGA_API_ENDPOINT,sprint);
        List<JsonNode> newTasks = new ArrayList<>();
        for(JsonNode node: allTasks){
            JsonNode nodeStatus = node.get("status_extra_info");
            if(nodeStatus != null && nodeStatus.has("name")){
                if(nodeStatus.get("name").asText().contains("New")){
                    newTasks.add(node);
                }
            }
        }
        return newTasks;
    }
}
