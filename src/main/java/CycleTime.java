import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


class Pair<T, U> {
    public final T key;
    public final U value;
    public Pair(T key, U value) {
        this.key = key;
        this.value = value;
    }
}
public class CycleTime {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return LocalDateTime.parse(dateTimeString, formatter);
    }


    static Map<String,List<Integer>> getMatrixData(int projectId, String authToken, String TAIGA_API_ENDPOINT){

        List<JsonNode> tasks = Tasks.getClosedTasks(projectId, authToken,TAIGA_API_ENDPOINT);
        Map<String,List<Integer>> cycleTime = calculateAndPrintCycleTime(tasks,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> sortedCycleTime = new TreeMap<>(cycleTime);
        Map<String, List<Integer>> orderedCycleTime = new LinkedHashMap<>(sortedCycleTime);
//        for (Map.Entry<String, List<Integer>> entry : orderedCycleTime.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
        return orderedCycleTime;
    }
    private static Map<String,List<Integer>> calculateAndPrintCycleTime(List<JsonNode> tasks,String authToken,String TAIGA_API_ENDPOINT) {
        Map<String,List<Integer>> cycleTime = new HashMap<>();

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

                System.out.println("Task ID: " + task.path("subject").asText() + ", Cycle Time: " + cycleTimeAndClosedTasks[0] + " days");

                if(cycleTime.containsKey(date)) {
                    List<Integer> temp = cycleTime.get(date);
                    temp.add(cycleTimeAndClosedTasks[0]);
                    cycleTime.put(date, temp);
                }else{
                    List<Integer> temp = new ArrayList<>();
                    temp.add(cycleTimeAndClosedTasks[0]);
                    cycleTime.put(date,temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return cycleTime;
    }
}