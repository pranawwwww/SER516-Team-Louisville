import TaskExcess.TaskExcess;
import org.junit.jupiter.api.Test;
import Authentication.Authentication;
import static org.junit.jupiter.api.Assertions.*;

public class TaskExcessTest {
    int projectId = 1521720;
    String authToken = Authentication.authenticate("louisville_test", "SER516");
    String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
    String sprint = "Sprint 2";
    TaskExcess actual=new TaskExcess(authToken,TAIGA_API_ENDPOINT,projectId,sprint);
    int expected;

    @Test
    void testGetNewTask(){
        expected=0;
        assertEquals(expected,actual.getNewTasks());
    }

    @Test
    void testGetNumberOfTotalTasks(){
        expected=22;
        assertEquals(expected,actual.getNumberOfTotalTasks());
    }

    @Test
    void testGetTaskExcess(){
        expected=0;
        assertEquals(expected,actual.getTaskExcess());
    }
}
