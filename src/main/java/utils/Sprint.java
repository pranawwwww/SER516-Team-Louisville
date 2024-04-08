package utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;

import java.util.Scanner;

public class Sprint {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static void getMilestoneList(String authToken,String TAIGA_API_ENDPOINT,int projectId) {

        String endpoint = TAIGA_API_ENDPOINT + "/milestones?project=" + projectId;

        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        if (responseJson != null) {
            try {
                JsonNode milestoneInfo = objectMapper.readTree(responseJson);
                for (JsonNode jsonObject : milestoneInfo) {
        
                    String name = jsonObject.get("name").asText();
                    int id = jsonObject.get("id").asInt();
        
                    System.out.println("Name: " + name + ", ID: " + id);
                    
                }
                System.out.println("Enter the name of the sprint");
                String sprint= scanner.nextLine(); 
                
                for (JsonNode jsonObject : milestoneInfo) {

        
                    String name = jsonObject.get("name").asText();
                    int id = jsonObject.get("id").asInt();

                    if(sprint.equals(name)){
                        getMilestoneStats(authToken, TAIGA_API_ENDPOINT, projectId, id);
                    }
        

                    
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void getMilestoneStats(String authToken,String TAIGA_API_ENDPOINT,int projectId, int milestoneId){
        String endpoint = TAIGA_API_ENDPOINT + "/milestones/"+ milestoneId +"/stats";

        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

   

        String responseJson = HTTPRequest.sendHttpRequest(request); 

        if(responseJson!=null){
            try{
                JsonNode milestoneStats = objectMapper.readTree(responseJson);
                System.out.println(milestoneStats);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}