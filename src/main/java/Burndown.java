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
import java.util.List;
import java.util.Scanner;



public class Burndown {

    private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static String promptUser(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String promptUserPassword(String prompt) {
        if (System.console() != null) {
            char[] passwordChars = System.console().readPassword(prompt);
            return new String(passwordChars);
        } else {
            System.out.print(prompt);
            return scanner.nextLine();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    public static List<JsonNode> getMilestoneStats(String authToken,String TAIGA_API_ENDPOINT,int projectId,String sprint) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statList;
    }
}
