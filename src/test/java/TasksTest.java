import org.junit.jupiter.api.Test;
import Authentication.Authentication;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.HttpGet;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utils.HTTPRequest;
import utils.Tasks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TasksTest {

    @Test
    public void getUnfinishedTasks() {
        int projectId = 1525366;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        String sprint = "Sprint 1";

        // Adjusted mocked response JSON to include necessary fields and values
        String mockedResponseJson = "[{\"id\":1,\"is_closed\":false},{\"id\":2,\"is_closed\":true},{\"id\":3,\"is_closed\":false}]";

        // Mocking the HTTPRequest class
        try (MockedStatic<HTTPRequest> mockedStatic = Mockito.mockStatic(HTTPRequest.class)) {
            // Mocking the static method
            mockedStatic.when(() -> HTTPRequest.sendHttpRequest(Mockito.any(HttpGet.class)))
                    .thenReturn(mockedResponseJson);

            // Calling the method under test
            List<JsonNode> unfinishedTasks = Tasks.getUnfinishedTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);

            // Verifying the result
            assertEquals(2, unfinishedTasks.size());
            assertEquals(1, unfinishedTasks.get(0).get("id").asInt());
            assertFalse(unfinishedTasks.get(0).get("is_closed").asBoolean());
        }
    }

    @Test
    public void testGetAllTasks() {
        // Mocking parameters
        int projectId = 1525366;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        String sprint = "Sprint 1";

        // Mocking the HTTPRequest class
        try (MockedStatic<HTTPRequest> mockedStatic = Mockito.mockStatic(HTTPRequest.class)) {
            // Mocking the static method
            String mockedResponseJson = "[{\"id\":1,\"name\":\"Task 1\"},{\"id\":2,\"name\":\"Task 2\"},{\"id\":3,\"name\":\"Task 3\"}]";
            mockedStatic.when(() -> HTTPRequest.sendHttpRequest(Mockito.any(HttpGet.class)))
                    .thenReturn(mockedResponseJson);

            // Calling the method under test
            List<JsonNode> allTasks = Tasks.getAllTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);

            // Verifying the result
            assertEquals(3, allTasks.size());
            assertEquals(1, allTasks.get(0).get("id").asInt());
            assertEquals("Task 1", allTasks.get(0).get("name").asText());
        }
    }
}
