import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CycleTimeTest {

    @Test
    void testGetCycleTimePerTask1() {

        int projectId = 1520578;
        String authToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzA3Njg5NDk1LCJqdGkiOiI3MzcyOTI5ZDIyZjE0NThkYmZiMTkyMDQ3ODdmZDU2MCIsInVzZXJfaWQiOjYxNzI0MH0.Gi6ShLaLe9ozMlxWqqxsqnd0XFop1J4HdvQFyFISHzJzLVqFqpWsnJQXURx9czElbRSPOfzk7OJw8MetrDDbIGgKS3-bJD7voeLml-F9ikAYocB3gJC1zo4YfVj037EKB4wi1T-dAgvxeAgoNbNpoGjYCeSTu4PBHxlKm4SMNvCiazkZTJzvPTqGbyWAN3ryW-lyifS1XCSM6qY3_CNsuHW1Qvj2mdR21e3OBMPcmFvKE5lZmyMvAhLinghwfpdMGh-wRBINezSvzQYiEuFmbmBACSim2kbG35AVjVbKA6pgcRFfZXdnlY3zJq51VgePfF5_3gZz6lYEiK3MUh7zLg";
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Integer>> input = CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> expected = new HashMap<>();
        expected.put("2024-01-26", Arrays.asList(0, 0, 0, 0, 0));
        expected.put("2024-01-31", Arrays.asList(5, 0, 0));
        assertEquals(expected,input,"Test Passed!!");
    }
    @Test
    void testGetCycleTimePerTask2() {

        int projectId = 1525366;
        String authToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzA3NjkwNTU5LCJqdGkiOiIyMjRiNjU1NzU3Njc0YWM1YTgwMWY5YmVhYzI1ZTg1YSIsInVzZXJfaWQiOjYxNzI0MH0.VSDx0oqAZzGw2rOtyDOAfTW-sT8i8Tia58iKv0ZzFbPWLz0uXqIsMSbqgnQZcD3jSyJxaDN9B_mFfkmEkc8FsGJGLGS33uOhFIE9-8QQFmnYcx5wbhDWBTHjct8HcHsor2j21bgcZEUjBzCZKfDm0UH2wkyZP9DgFp4W3i9Kz3xUNhWhSR2t8UDezK9ULZpIRFy8jEO-bOxAo16jmjZDCU-J_RPhTSFPJQg7QGWSliL0TOuoxL19O2RxtSYP0KJPo0Ip8ZnVj_sRniIWgMRJv8-pTkp6IS7yWd8quo9jAuQxWTR1J7On_iA-qbUtFUNUwDZZMXP_wxKwX_sVTzSiEA";
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Integer>> input = CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> expected = new HashMap<>();
        expected.put("2024-02-10", Arrays.asList(0, 14, 7, 7, 0, 0, 0, 0, 0, 7));
        assertEquals(expected,input,"Test Passed!!");
    }
    @Test
    void testGetCycleTimePerTask3() {

        int projectId = 435060;
        String authToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzA3NjkwNzQzLCJqdGkiOiI1NWQxOTUwOTg1M2I0MTc4OTRhNDIxMmI5NmU1Mzg3YSIsInVzZXJfaWQiOjYxNzI0MH0.T8FgMvZfMulnkLj7T5B4FUzfHVjpu2BJAyGPRjs32MwaR5hUuUsOja9G0F2yyLkZv0G3jtO-qCkctl613ZVTlfOdrObtyDC2B5jOnub6V2vZGarbCMPUldqusiV5Ql8wCCplmUq33J46JsZYEF8ZtljsFoS4lcuOmeyrqgFkv_WwZn1YCuZ1Tg5SVIG6GJ-ReLmjhLciV6H7DwcYL07ILD3z5JdFF_vMIueEy5Sg5KvLUZrCB3J8EA91-Y-5CpxKMza3dBud8VEA3cNSdwUGZKglUyOOmyIQbYD7SGWH9Mkw-hFII7JFBzJMKyN0gfOgOAOMJo6x71nEwYQJukHLNw";
        String TAIGA_API_ENDPOINT = "https://api.taiga.io/api/v1";

        Map<String, List<Integer>> input = CycleTime.getMatrixData(projectId,authToken,TAIGA_API_ENDPOINT);
        Map<String, List<Integer>> expected = new HashMap<>();
        assertEquals(expected,input,"Test Passed!!");
    }
}
