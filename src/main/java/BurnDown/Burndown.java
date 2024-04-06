package BurnDown;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utils.SprintData;
import utils.SprintUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Burndown {
    private static final ListProperty<BurnDownDataPoint> progress = new SimpleListProperty<>(FXCollections.observableArrayList());
    private static final StringProperty selectedSprint = new SimpleStringProperty();

    public static ListProperty<BurnDownDataPoint> progressProperty() {
        return progress;
    }

    public static ObservableList<BurnDownDataPoint> getProgress() {
        return progress.get();
    }

    public static void setProgress(ObservableList<BurnDownDataPoint> value) {
        progress.set(value);
    }

    public static StringProperty selectedSprintProperty() {
        return selectedSprint;
    }

    public static String getSelectedSprint() {
        return selectedSprint.get();
    }

    public static void setSelectedSprint(String value) {
        selectedSprint.set(value);
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
