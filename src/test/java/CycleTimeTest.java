import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import Authentication.Authentication;
import CycleTime.CycleTime;

import java.util.ArrayList;
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

        Map<String, List<Pair<String, Integer>>> input = CycleTime.getMatrixData(projectId, authToken, TAIGA_API_ENDPOINT);
        Map<String, List<Pair<String, Integer>>> expected = new HashMap<>();

        // Adding specific values for the dates
        List<Pair<String, Integer>> taskList1 = new ArrayList<>();
        taskList1.add(Pair.of("Task 1", 0));
        taskList1.add(Pair.of("Task 1", 0));
        taskList1.add(Pair.of("Task 1", 0));
        taskList1.add(Pair.of("Task 1", 0));
        taskList1.add(Pair.of("Task 1", 0));
        expected.put("2024-01-26", taskList1);

        List<Pair<String, Integer>> taskList2 = new ArrayList<>();
        taskList2.add(Pair.of("Task 1", 5));
        taskList2.add(Pair.of("Task 1", 0));
        taskList2.add(Pair.of("Task 1", 0));
        expected.put("2024-01-31", taskList2);

        assertEquals(expected, input, "Test Passed!!");
    }
    @Test
    void testGetCycleTimePerTask2() {

        int projectId = 1525366;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";


        Map<String, List<Pair<String, Integer>>> input = CycleTime.getMatrixData(projectId, authToken, TAIGA_API_ENDPOINT);
        Map<String, List<Pair<String, Integer>>> expected = new HashMap<>();

        // Adding specific values for the date
        List<Pair<String, Integer>> taskList = new ArrayList<>();
        taskList.add(Pair.of("Task1", 0));
        taskList.add(Pair.of("Task2", 14));
        taskList.add(Pair.of("Task1", 7));
        taskList.add(Pair.of("Task1", 7));
        taskList.add(Pair.of("Task2", 0));
        taskList.add(Pair.of("Task1", 0));
        taskList.add(Pair.of("Task2", 0));
        taskList.add(Pair.of("Task1", 0));
        taskList.add(Pair.of("Task2", 0));
        taskList.add(Pair.of("Task1", 7));
        expected.put("2024-02-10", taskList);

        assertEquals(expected, input, "Test Passed!!");
    }
    @Test
    void testGetCycleTimePerTask3() {

        int projectId = 435060;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Pair<String, Integer>>> input = CycleTime.getMatrixData(projectId, authToken, TAIGA_API_ENDPOINT);
        Map<String, List<Pair<String, Integer>>> expected = new HashMap<>();

        assertEquals(expected, input, "Test Passed: Both maps are empty");
    }
}
