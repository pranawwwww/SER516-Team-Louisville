import org.junit.jupiter.api.Test;

import Authentication.Authentication;
import TaskChurn.TaskChurn;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskChurnTest {

    @Test
    void testCalculateTaskChurn() {
        int projectId = 1521720;
        String authToken = Authentication.authenticate("louisville_test", "SER516");
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";
        Map<LocalDate,Float> input =TaskChurn.calculateTaskChurn(projectId,authToken,TAIGA_API_ENDPOINT,"Sprint 1");
        Map<LocalDate, Float> expected = new HashMap<>();
        expected.put(LocalDate.of(2024, 1, 31), (float) 0.0);
        expected.put(LocalDate.of(2024, 2, 2), (float) 114.28572);
        expected.put(LocalDate.of(2024, 2, 3), (float) 85.71429);
        expected.put(LocalDate.of(2024, 2, 4), (float) 42.857143);
        expected.put(LocalDate.of(2024, 2, 5), (float) 100.0);
        expected.put(LocalDate.of(2024, 2, 6), (float) 71.42857);
        expected.put(LocalDate.of(2024, 2, 7), (float) 42.857143);
        expected.put(LocalDate.of(2024, 2, 9), (float) 14.285715);
        expected.put(LocalDate.of(2024, 2, 10), (float) 14.285715);
        expected.put(LocalDate.of(2024, 2, 11), (float) 128.57143);
        expected.put(LocalDate.of(2024, 2, 12), (float) 100.0);
        expected.put(LocalDate.of(2024, 2, 14), (float) 100.0);
        expected.put(LocalDate.of(2024, 2, 16), (float) 14.285715);
        expected.put(LocalDate.of(2024, 2, 17), (float) 14.285715);
        expected.put(LocalDate.of(2024, 2, 18), (float) 85.71429);
        TreeSet<LocalDate> allKeys = new TreeSet<>();
        allKeys.addAll(expected.keySet());
        for(LocalDate key: allKeys){
            assertEquals(input.get(key),  expected.get(key));
        }
        
    }
}
