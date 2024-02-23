package BurnDown;
import com.fasterxml.jackson.databind.JsonNode;

import utils.SprintData;
import utils.SprintUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Burndown {


    private static final Scanner scanner = new Scanner(System.in);

    private static List<BurnDownDataPoint> progress = new ArrayList<>();

    public static List<BurnDownDataPoint> getProgress() {
        return progress;
    }

    public static String promptSprint(String prompt){
        System.out.print(prompt);
        return scanner.nextLine();
    }


    public static List<BurnDownDataPoint> getBurnDownProgress(String authToken,String TAIGA_API_ENDPOINT,int projectId,String sprint) {
        try{
            if(!progress.isEmpty()){
                progress.clear();
            }
            
            SprintData sprintData = SprintUtils.getSprintDetails(authToken,TAIGA_API_ENDPOINT,projectId,sprint);
            JsonNode progressNode = sprintData.getProgressNode();
            for(JsonNode node:progressNode){
                String day = node.get("day").asText();
                double openPoints = node.get("open_points").asDouble();
                double optimalPoints = node.get("optimal_points").asDouble();
                progress.add(new BurnDownDataPoint(day,openPoints,optimalPoints));
            }
            return progress;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
