import org.junit.jupiter.api.Test;
import Authentication.Authentication;
import TaskDefectDensity.TaskDefectDensity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals(100, taskDefectDensity.getTaskDefectDensity(), 0.01);
    }


}
