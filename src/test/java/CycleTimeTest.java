import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import Authentication.Authentication;
import CycleTime.CycleTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CycleTimeTest {

    @Test
    void testGetCycleTimePerTask1() {

        int projectId = 1520578;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Pair<String, Integer>>> input = CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> expected = new HashMap<>();
        expected.put("2024-01-26", Arrays.asList(0, 0, 0, 0, 0));
        expected.put("2024-01-31", Arrays.asList(5, 0, 0));
        assertEquals(expected,input,"Test Passed!!");
    }
    @Test
    void testGetCycleTimePerTask2() {

        int projectId = 1525366;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Pair<String, Integer>>> input = CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> expected = new HashMap<>();
        expected.put("2024-02-10", Arrays.asList(0, 14, 7, 7, 0, 0, 0, 0, 0, 7));
        assertEquals(expected,input,"Test Passed!!");
    }
    @Test
    void testGetCycleTimePerTask3() {

        int projectId = 435060;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Pair<String, Integer>>> input = CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> expected = new HashMap<>();
        assertEquals(expected,input,"Test Passed!!");
    }
}
