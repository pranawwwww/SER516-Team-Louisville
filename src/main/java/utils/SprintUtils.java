package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SprintUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static String start_date;
    private static String end_date;
    private static double total_points;
    private static List<String> sprints = new ArrayList<>();
    private static JsonNode progressNode;


    public static List<String> getSprints(){
        return sprints;
    }
    
    public static List<JsonNode> getMilestoneList(String authToken,String TAIGA_API_ENDPOINT,int projectId) {

        List<JsonNode> list = new ArrayList<>();
        String endpoint = TAIGA_API_ENDPOINT + "/milestones?project=" + projectId;
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(endpoint);
            request.setHeader("Authorization", "Bearer " + authToken);
            request.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(request);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonNode milestones = objectMapper.readTree(result.toString());
            for (JsonNode milestone : milestones) {
                list.add(milestone);
            }
            
            for (JsonNode milestone : list) {
                sprints.add(milestone.get("name").textValue());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    public static SprintData getSprintDetails(String authToken,String TAIGA_API_ENDPOINT,int projectId,String sprint) {

        List<JsonNode> list = getMilestoneList(authToken, TAIGA_API_ENDPOINT, projectId);        
        List<JsonNode> statList = new ArrayList<>();
        int mileStoneId = -1;
        for (JsonNode milestone : list) {
            if(sprint.equals(milestone.get("name").textValue())) {
                mileStoneId = milestone.get("id").asInt();
                break;
            }
        }

        String endpoint = TAIGA_API_ENDPOINT + "/milestones/" + mileStoneId + "/stats";
        try{
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(endpoint);
            request.setHeader("Authorization", "Bearer " + authToken);
            request.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(request);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonNode milestones = objectMapper.readTree(result.toString());
            for (JsonNode milestone : milestones) {
                statList.add(milestone);
            }
            start_date=statList.get(1).asText();
            end_date=statList.get(2).asText();

            JsonNode points=statList.get(3);
            Iterator<String> fields=points.fieldNames();
            total_points=points.get(fields.next()).asDouble();
            progressNode = statList.get(statList.size() - 1);
            return new SprintData(start_date, end_date, total_points, progressNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
