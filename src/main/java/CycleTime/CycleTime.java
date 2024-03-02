package CycleTime;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import utils.HTTPRequest;
import utils.Tasks;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.commons.lang3.tuple.Pair;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class CycleTime {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }


    public static Map<String, List<Pair<String, Integer>>> getMatrixData(int projectId, String authToken, String TAIGA_API_ENDPOINT,String sprint){
        List<JsonNode> tasks = Tasks.getClosedTasks(projectId, authToken,TAIGA_API_ENDPOINT,sprint);
        Map<String, List<Pair<String, Integer>>> cycleTime = calculateAndPrintCycleTime(tasks,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Pair<String, Integer>>> sortedCycleTime = new TreeMap<>(cycleTime);
        Map<String, List<Pair<String, Integer>>> orderedCycleTime = new LinkedHashMap<>(sortedCycleTime);

        return orderedCycleTime;
    }
    private static Map<String, List<Pair<String, Integer>>> calculateAndPrintCycleTime(List<JsonNode> tasks,String authToken,String TAIGA_API_ENDPOINT) {
        Map<String, List<Pair<String, Integer>>> cycleTime = new HashMap<>();

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

                String date = (""+finishedDate).substring(0, (""+finishedDate).indexOf('T'));

                int[] cycleTimeAndClosedTasks = Tasks.calculateCycleTime(historyData, finishedDate);

                if (cycleTime.containsKey(date)) {
                    List<Pair<String, Integer>> temp = cycleTime.get(date);
                    temp.add(Pair.of(task.path("subject").asText(), cycleTimeAndClosedTasks[0]));
                    cycleTime.put(date, temp);
                } else {
                    List<Pair<String, Integer>> temp = new ArrayList<>();
                    temp.add(Pair.of(task.path("subject").asText(), cycleTimeAndClosedTasks[0]));
                    cycleTime.put(date, temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return cycleTime;
    }
}