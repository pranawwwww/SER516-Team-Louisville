import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Authentication.Authentication;
import utils.Tasks;
import TaskDefectDensity.TaskDefectDensity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class TaskDefectDensityTest {
    @Test
    public void testAuthToken(){
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        assertNotNull(authToken,"Test Passed!");
    }
    @Test
    public void testTaskDefectDensityCalculation() {
        // Initialize your class with mocked data
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        TaskDefectDensity taskDefectDensity = new TaskDefectDensity(authToken, TAIGA_API_ENDPOINT, 1520578, "Sprint 2");

        // Assert the calculations are as expected
        assertEquals(3, taskDefectDensity.getNumberOfTotalTasks());
        assertEquals(0, taskDefectDensity.getNumberOfUnfinishedTasks());
        assertEquals(3, taskDefectDensity.getNumberOfDeletedTasks());
        assertEquals(66.67, taskDefectDensity.getTaskDefectDensity(), 0.01);
    }


}
