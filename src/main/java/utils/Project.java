package utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Project {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static int getProjectId(String authToken,String TAIGA_API_ENDPOINT,String projectSlug) {

        String endpoint = TAIGA_API_ENDPOINT + "/projects/by_slug?slug=" + projectSlug;

        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        if (responseJson != null) {
            try {
                JsonNode projectInfo = objectMapper.readTree(responseJson);
                int projectId = projectInfo.has("id") ? projectInfo.get("id").asInt() : -1;

                if (projectId != -1) {
                    System.out.println("Project details retrieved successfully.");
                    return projectId;
                } else {
                    System.out.println("Invalid project slug. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -1;
    }
    public static LocalDate getProjectStartDate(String authToken,String TAIGA_API_ENDPOINT, int projectId) {
        JsonNode projectDetails = null;
        String endpoint = TAIGA_API_ENDPOINT + "/projects/" + projectId;
        HttpGet request = new HttpGet(endpoint);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String responseJson = HTTPRequest.sendHttpRequest(request);

        try {
            projectDetails = objectMapper.readTree(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String createdAt = projectDetails.get("created_date").asText();
        LocalDate createdDate = parseDateTime(createdAt).toLocalDate();
        return createdDate;
    }

        private static LocalDateTime parseDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
