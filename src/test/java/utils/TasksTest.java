package utils;
import org.junit.jupiter.api.Test;
import Authentication.Authentication;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.HttpGet;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import java.util.List;

import static org.junit.Assert.assertEquals;

class TasksTest {

    @Test
    void getUnfinishedTasks() {
        int projectId = 1525366;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        String sprint = "Sprint 1";

        // Adjusted mocked response JSON to include necessary fields and values
        String mockedResponseJson = "[{\"id\":1,\"is_closed\":false},{\"id\":2,\"is_closed\":true},{\"id\":3,\"is_closed\":false}]";

        Mockito.mockStatic(HTTPRequest.class);
        Mockito.when(HTTPRequest.sendHttpRequest(ArgumentMatchers.any(HttpGet.class)))
                .thenReturn(mockedResponseJson);

        // Calling the method under test
        List<JsonNode> unfinishedTasks = Tasks.getUnfinishedTasks(projectId, authToken, TAIGA_API_ENDPOINT, sprint);

        // Verifying the result
        assertEquals(2, unfinishedTasks.size());
        assertEquals(1, unfinishedTasks.get(0).get("id").asInt());
        assertEquals(false, unfinishedTasks.get(0).get("is_closed").asBoolean());
    }

}