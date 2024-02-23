package utils;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class SprintData {
    private String start_date;
    private String end_date;
    private double total_points;
    private List<String> sprints = new ArrayList<>();
    private JsonNode progressNode;

    public String getStart_date(){
        return this.start_date;
    }
    public String getEnd_date() {
        return this.end_date;
    }

    public double getTotal_points() {
        return this.total_points;
    }

    public List<String> getSprints(){
        return this.sprints;
    }

    public JsonNode getProgressNode(){
        return this.progressNode;
    }

    public SprintData(String start_date, String end_date, double total_points, JsonNode progressNode){
        this.start_date=start_date;
        this.end_date=end_date;
        this.total_points=total_points;
        this.sprints = sprints != null ? sprints : new ArrayList<>();
        this.progressNode = progressNode;
    }
}
