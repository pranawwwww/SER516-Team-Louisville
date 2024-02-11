import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Pair<T, U> {
    public final T key;
    public final U value;
    public Pair(T key, U value) {
        this.key = key;
        this.value = value;
    }
}
class CycleTimeTest {
    CycleTime ct =  new CycleTime();
    @Test
    void testCycleTimeMatrix1(){
        List<Pair<String, Integer>> result = ct.getMatrixData(1521720,"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzA2OTM1MTk0LCJqdGkiOiI2MDljZWIxMmQxNTc0ZjI2OWViNzI0MTVmMGMzYjgwMCIsInVzZXJfaWQiOjYxNzI0MH0.drrpLEIKvOmfnzE4rTBXJRcc5aIzDH6GpCByWWnfAPa_vS0GfgbrxWgKur87UjpBMXjB0xvnwDFnyUN1PWYVo1VXggm2Hi03toPIkhjVtEyXE_EdrT5633wNkTIQwGOUJEi73A4ZO_U0iBMovkWvn-4KzS1LZhN0DzUImpOIwqfTS6lEHlV7tCraYwqPZJv9pXh5efX-CKaCMSTZBsfpz5ERtDM3eHWYQk1QK1OC8v7_c4X-ZvnChO1mD5VwXWhVsqf9LCPtl-w5VFWp_wNC7CFFcOaWPPzc6vJnx3lcb6713w4z_Ft1GvvItXKqJtNJs324dj0qMWm0s6ZqNatSaw","https://api.taiga.io/api/v1");
        List<Pair<String, Integer>> expected  = new ArrayList<Pair<String, Integer>>();
        assertEquals(expected,result);
    }
    @Test
    void testCycleTimeMatrix2(){
        List<Pair<String, Integer>> result = ct.getMatrixData(435060,"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzA2OTM1NjU1LCJqdGkiOiI4ZmI5NTQ4MzQwOTg0Zjc1OWRmNGVlYmIwZmVjYWQwZSIsInVzZXJfaWQiOjYxNzI0MH0.ayTYsaNUifNelg8bOjYSVmTGRXlK0z_mydhq70qJ1HS3dBS2XC9c2S2EOVigIxwH-bXvPKbwLHs1R3RRVWEKffCkDi3B7YeYP68uhDP8rPlZAdDILmpPYxUwtb3byGB3ClR6fgF4MqrT1iCZEazsCbA3pyHJj-sEOlpiQncKzCvJmoGzWLWHqRFI9n9sLWJ6L8g_e3a_zPcnejyyPTHSggLAQZa6v7B6qLZnpKYd0EvrpYflRXe08R-GKRJ2Ft2Z5hGPrGKPxJ_QPP9fXp61jocyBFVFmqHi_UVh9DYj3tu9eZEA5Re7WdcoDTg9fXPJyQ8fAQPellq5ZYrwfANdUA","https://api.taiga.io/api/v1");
        List<Pair<String, Integer>> expected  = new ArrayList<Pair<String, Integer>>();
        assertEquals(expected,result);
    }
}