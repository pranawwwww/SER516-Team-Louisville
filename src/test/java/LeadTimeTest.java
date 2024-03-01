import org.junit.jupiter.api.Test;

import Authentication.Authentication;
import LeadTime.LeadTime;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeadTimeTest {

    @Test
    void testGetLeadTimePerTask() {
        int projectId = 1521720;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, Map<String, Object>> input = LeadTime.getLeadTimePerTask(projectId,authToken,TAIGA_API_ENDPOINT, "Sprint 1");

        Map<String, Object> expected = new HashMap<>();
        expected.put("userStoryName", "Select a Taiga project to apply this metric to.");
        expected.put("epicName", "Burndown chart");
        expected.put("endDate", "2024-02-02");
        expected.put("taskName", "Researching on the application GUI");
        long leadTime  = 1;
        expected.put("leadTimeInDays",leadTime);
        expected.put("startDate", "2024-01-31");
        
        Map<String,Object> data = input.get("5330893: Researching on the application GUI");

        assertEquals(expected.get("userStoryName"), data.get("userStoryName"));
        assertEquals(expected.get("epicName"), data.get("epicName"));
        assertEquals(expected.get("endDate").toString(), data.get("endDate").toString());
        assertEquals(expected.get("taskName"), data.get("taskName"));
        assertEquals(expected.get("leadTimeInDays"),data.get("leadTimeInDays"));
        assertEquals(expected.get("startDate").toString(), data.get("startDate").toString());
    }
}
