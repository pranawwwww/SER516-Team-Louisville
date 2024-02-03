import com.fasterxml.jackson.databind.JsonNode;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
class Pair<T, U> {
    public final T key;
    public final U value;
    public Pair(T key, U value) {
        this.key = key;
        this.value = value;
    }
}
public class CycleTime {
    static List<Pair<String, Integer>> getMatrixData(int projectId, String authToken, String TAIGA_API_ENDPOINT){
        List<JsonNode> tasks = Tasks.getClosedTasks(projectId, authToken,TAIGA_API_ENDPOINT);

        return calculateAndPrintCycleTime(tasks,authToken,TAIGA_API_ENDPOINT);
    }
    private static List<Pair<String, Integer>> calculateAndPrintCycleTime(List<JsonNode> tasks, String authToken, String TAIGA_API_ENDPOINT) {
        return new ArrayList<Pair<String, Integer>>();
    }
}
