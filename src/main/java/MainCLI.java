// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// import Authentication.Authentication;
// import Authentication.AuthenticationGUI;
// import BurnDown.Burndown;
// import CycleTime.CycleTime;
// import LeadTime.LeadTime;
// import utils.GlobalData;
// import utils.HTTPRequest;
// import utils.Project;
// import utils.SprintData;
// import utils.SprintUtils;
// import utils.Tasks;

// import org.apache.http.HttpHeaders;
// import org.apache.http.client.methods.HttpGet;
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Scanner;


// public class MainCLI {

//     private static final String TAIGA_API_ENDPOINT = GlobalData.getTaigaURL();
//     private static final Scanner scanner = new Scanner(System.in);
//     private static final ObjectMapper objectMapper = new ObjectMapper()
//             .registerModule(new JavaTimeModule())
//             .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

//     public static void main(String[] args) {

//         while(true){
//             String choice = promptUser("enter choice:\n"+
//             "(1)Launch GUI\n"+
//             "(2)Launch CLI\n"+
//             "(3)exit\n");
//             switch (choice) {
//                 case "1":
//                     AuthenticationGUI.launch(AuthenticationGUI.class,args);
//                 case "2":
//                     // Prompting user for Taiga's username and password
//                     String taigaUsername = promptUser("Enter your Taiga username: ");
//                     String taigaPassword = promptUserPassword("Enter your Taiga password: ");
//                     String authToken = Authentication.authenticate(taigaUsername, taigaPassword);
//                     if (authToken != null) {
//                         System.out.println("Authentication successful.");
                             
//                     String projectSlug = promptUser("Enter the Taiga project slug: ");
//                     // Calling Taiga API to get project details
//                     int projectId = Project.getProjectId(authToken,TAIGA_API_ENDPOINT, projectSlug);

//                     if (projectId != -1) {
//                         handleUserAction(projectId, authToken, scanner);
//                         }
//                     }
//                 case "3":
//                     System.out.println("Exiting...");
//                     return;
//                 default:
//                     System.out.println("Invalid choice. Please enter a valid option.");

//             }
//         }
        
        

//     }

//     private static String promptUser(String prompt) {
//         System.out.print(prompt);
//         return scanner.nextLine();
//     }

//     private static String promptUserPassword(String prompt) {
//         if (System.console() != null) {
//             char[] passwordChars = System.console().readPassword(prompt);
//             return new String(passwordChars);
//         } else {
//             System.out.print(prompt);
//             return scanner.nextLine();
//         }
//     }

//     private static void handleUserAction(int projectId, String authToken, Scanner scanner) {
//         while (true) {
//             String action = promptUser(
//                     "What do you want to do next?\n" +
//                             "(1) Show open user stories\n" +
//                             "(2) Calculate number of tasks closed per week metric\n" +
//                             "(3) Calculate average lead time\n" +
//                             "(4) Calculate average cycle time\n" +
//                             "(5) Calculate lead time per user story\n" +
//                             "(6) Calculate cycle time per user story\n" +
//                             "(7) Fetch Sprint details for burndown\n" +
//                             "(8) Exit\n" +
//                             "Enter action: ");

//             switch (action) {
//                 case "1":
//                     System.out.println("Getting list of all open user stories...");

//                     // Get list of open user stories in a project.
//                     getOpenUserStories(projectId, authToken);
//                     break;

//                 case "2":
//                     System.out.println("Calculating throughput metric...");

//                     // Get all closed tasks per week
//                     getClosedTasksPerWeek(projectId, authToken);
//                     break;

//                 case "3":
//                     System.out.println("Calculating average lead time...");
//                     getLeadTime(projectId, authToken);
//                     break;
//                 case "4":
//                     System.out.println("Calculating average cycle time...");
//                     getCycleTime(projectId, authToken);
//                     break;
//                 case "5":
//                     System.out.println("Calculate and display lead time per user story...") ;
//                     LeadTime.getLeadTimePerTask(projectId, authToken, TAIGA_API_ENDPOINT,"Sprint 1" );
//                     break;

//                 case "6":
//                     System.out.println("Calculating cycle time of each user stories...");
//                     CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
//                     break;
                
//                 case "7":

//                     System.out.println("Fetching Sprint details for burndown...");
//                     fetchSprintDetails(projectId,authToken);
//                     break;

//                 case "8":
//                     System.out.println("Exiting...");
//                     return;

//                 default:
//                     System.out.println("Invalid choice. Please enter a valid option.");
//             }
//         }
//     }

//     private static void getOpenUserStories(int projectId, String authToken) {

//         // Taiga endpoint to get list of all open user stories.
//         String endpoint = TAIGA_API_ENDPOINT + "/userstories?project=" + projectId;

//         HttpGet request = new HttpGet(endpoint);
//         request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
//         request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

//         // Making an API call with the above endpoint and getting the response.
//         String responseJson = HTTPRequest.sendHttpRequest(request);

//         try {
//             JsonNode userStoriesNode = objectMapper.readTree(responseJson);
//             List<String> openUserStories = new ArrayList<>();

//             for (JsonNode storyNode : userStoriesNode) {
//                 boolean isClosed = storyNode.has("is_closed") && storyNode.get("is_closed").asBoolean();
//                 if (!isClosed) {
//                     String name = storyNode.has("subject") ? storyNode.get("subject").asText() : "";
//                     String description = storyNode.has("description") ? storyNode.get("description").asText() : "";

//                     String storyDetails = String.format("{ \"name\": \"%s\", \"description\": \"%s\" }", name, description);
//                     openUserStories.add(storyDetails);
//                 }
//             }

//             String result = String.format("{ \"open_user_stories\": [%s] }", String.join(", ", openUserStories));
//             ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
//             String formattedJson = objectMapper.writeValueAsString(objectMapper.readTree(result));
//             System.out.println(formattedJson);

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private static LocalDateTime parseDateTime(String dateTimeString) {
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//         return LocalDateTime.parse(dateTimeString, formatter);
//     }

//     private static void getClosedTasksPerWeek(int projectId, String authToken) {
//         // Endpoint to get list of all tasks in a project.
//         String endpoint = TAIGA_API_ENDPOINT + "/tasks?project=" + projectId;

//         // Making an API call with the above endpoint.
//         HttpGet request = new HttpGet(endpoint);
//         request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
//         request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

//         try {
//             List<JsonNode> closedTasks = Tasks.getClosedTasks(projectId, authToken,TAIGA_API_ENDPOINT);
//             List<String> taskGroups = new ArrayList<>();

//             for (JsonNode task : closedTasks) {
//                 LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());
//                 LocalDateTime weekEnding = finishedDate.plusDays(6 - finishedDate.getDayOfWeek().getValue());

//                 String taskDetails = String.format("{ \"weekEnding\": \"%s\", \"closedTasks\": 1 }", weekEnding);
//                 taskGroups.add(taskDetails);
//             }

//             String result = String.format("{ \"closedTasksPerWeek\": [%s] }", String.join(", ", taskGroups));
//             ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
//             String formattedJson = objectMapper.writeValueAsString(objectMapper.readTree(result));
//             System.out.println(formattedJson);

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private static void getLeadTime(int projectId, String authToken) {
//         List<JsonNode> closedTasks = Tasks.getClosedTasks(projectId, authToken,TAIGA_API_ENDPOINT);

//         int leadTime = 0;
//         int closedTasksCount = 0;

//         for (JsonNode task : closedTasks) {
//             LocalDateTime createdDate = parseDateTime(task.get("created_date").asText());
//             LocalDateTime finishedDate = parseDateTime(task.get("finished_date").asText());

//             leadTime += Duration.between(createdDate, finishedDate).toDays();
//             closedTasksCount++;
//         }

//         double avgLeadTime = (closedTasksCount > 0) ? (double) leadTime / closedTasksCount : 0.0;
//         avgLeadTime = Math.round(avgLeadTime * 100.0) / 100.0;

//         System.out.println("\n***********************************\n");
//         System.out.println("Average Lead Time: " + avgLeadTime);
//         System.out.println("\n***********************************\n");
//     }

//     private static void getCycleTime(int projectId, String authToken) {
//         List<JsonNode> tasks = Tasks.getClosedTasks(projectId, authToken,TAIGA_API_ENDPOINT);
//         List<Integer> cycleTimeAndClosedTasks = Tasks.getTaskHistory(tasks, authToken, TAIGA_API_ENDPOINT);

//         int cycleTime = cycleTimeAndClosedTasks.get(0);
//         int closedTasks = cycleTimeAndClosedTasks.get(1);


//         double avgCycleTime = (closedTasks != 0) ? (double) cycleTime / closedTasks : 0;
//         avgCycleTime = Math.round(avgCycleTime * 100.0) / 100.0;

//         System.out.println("\n***********************************\n");
//         System.out.println("Average Cycle Time: " + avgCycleTime);
//         System.out.println("\n***********************************\n");
//     }

//     private static void fetchSprintDetails(int projectId, String authToken) {
//         String sprint = Burndown.promptSprint("Enter the sprint name: ");
//         //Burndown object to store sprint details
//         SprintData bd = SprintUtils.getSprintDetails(authToken, TAIGA_API_ENDPOINT, projectId, sprint);
//         System.out.println("Sprint details fetched");
//         System.out.println("start date: "+bd.getStart_date());
//         System.out.println("end date: "+bd.getEnd_date());
//         System.out.println("Total points: "+bd.getTotal_points());
//         System.out.println("Data Points:");
//         for(int i=0;i<bd.getProgress().size();i++){
//             System.out.println("Day: "+bd.getProgress().get(i).getDay()+", Open Points: "+bd.getProgress().get(i).getOpenPoints()+", Optimal Points: "+bd.getProgress().get(i).getOptimalPoints());
//         }
//     }
// }