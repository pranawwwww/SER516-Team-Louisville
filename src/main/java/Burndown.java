import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;



public class Burndown {


    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


    private static String start_date;
    private static String end_date;
    private static double total_points;
    private static List<BurnDownDataPoint> progress = new ArrayList<>();
    private static List<String> sprints = new ArrayList<>();

    public String getStart_date(){
        return start_date;
    }
    public static String getEnd_date() {
        return end_date;
    }

    public static double getTotal_points() {
        return total_points;
    }

    public static List<BurnDownDataPoint> getProgress() {
        return progress;
    }

    public static List<String> getSprints(){
        return sprints;
    }
    
    public Burndown(String start_date, String end_date, double total_points, List<BurnDownDataPoint> progress){
        this.start_date=start_date;
        this.end_date=end_date;
        this.total_points=total_points;
        this.progress = progress != null ? progress : new ArrayList<>();
    }

    public static String promptSprint(String prompt){
        System.out.print(prompt);
        return scanner.nextLine();
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

    public static Burndown getSprint(String authToken,String TAIGA_API_ENDPOINT,int projectId,String sprint) {

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
                statList.add(milestone);
            }
            start_date=statList.get(1).asText();
            end_date=statList.get(2).asText();

            JsonNode points=statList.get(3);
            Iterator<String> fields=points.fieldNames();
            total_points=points.get(fields.next()).asDouble();

            JsonNode progressNode = statList.get(statList.size() - 1);
            for(JsonNode node:progressNode){
                String day = node.get("day").asText();
                double openPoints = node.get("open_points").asDouble();
                double optimalPoints = node.get("optimal_points").asDouble();
                progress.add(new BurnDownDataPoint(day,openPoints,optimalPoints));
            }
            return new Burndown(start_date, end_date, total_points, progress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
