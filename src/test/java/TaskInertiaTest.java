import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import Authentication.Authentication;
import TaskInertia.TaskInertia;
import utils.Project;

public class TaskInertiaTest {

    @Test
    public void testGetTaskInertia(){
        int projectId = 1521720;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        LocalDate startDate = LocalDate.of(2024,3, 13);
        LocalDate endDate = LocalDate.of(2024, 3, 22);
        Map<LocalDate,Float> input = TaskInertia.getTaskInertia(projectId, authToken, TAIGA_API_ENDPOINT, startDate, endDate);
        Map<LocalDate,Float> expected = new TreeMap<>();
        for(LocalDate date: input.keySet()){
            System.out.println(date + "  " + input.get(date));
        }
        expected.put(LocalDate.of(2024,3, 13), 33.333336f);
        expected.put(LocalDate.of(2024,3, 14), 50.0f);
        expected.put(LocalDate.of(2024,3, 15), 50.0f);
        expected.put(LocalDate.of(2024,3, 16), 0.0f);
        expected.put(LocalDate.of(2024,3, 17), 0.0f);
        expected.put(LocalDate.of(2024,3, 18), 0.0f);
        expected.put(LocalDate.of(2024,3, 19), 1.0f);
        expected.put(LocalDate.of(2024,3, 20), 1.0f);
        expected.put(LocalDate.of(2024,3, 21), 16.666668f);
        expected.put(LocalDate.of(2024,3, 22), 5.0f);
        
        TreeSet<LocalDate> allKeys = new TreeSet<>();
        allKeys.addAll(expected.keySet());
        for(LocalDate key: allKeys){
            assertEquals(input.get(key),  expected.get(key));
        }
    }
}
