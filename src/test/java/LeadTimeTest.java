import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeadTimeTest {

    @Test
    void testGetLeadTimePerTask() {
        int projectId = 1520578;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, Map<String, Object>> input = LeadTime.getLeadTimePerTask(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, Map<String, Object>> expected = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        data.put("Name", "Task 1");
        data.put("startDate","2024-01-26T02:28:40.729");
        data.put("endDate","2024-01-31T01:54:43.045");
        data.put("leadTimeInDays",(long) 4);
        expected.put("5316287",data);
        assertEquals((expected.get("5316287")).get("leadTimeInDays"),(input.get("5316287")).get("leadTimeInDays"),"Test Passed!!");
    }
}
