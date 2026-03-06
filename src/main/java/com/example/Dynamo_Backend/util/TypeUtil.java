package com.example.Dynamo_Backend.util;

import java.util.HashMap;
import java.util.List;

public class TypeUtil {

    public static HashMap<String, Float> buildStatusHourMap(List<Object[]> results) {
        HashMap<String, Float> map = new HashMap<>();

        // init = 0
        List<String> statuses = List.of("R1", "R2", "E1", "E2", "S1", "S2", "0");
        for (String s : statuses) {
            map.put(s, 0f);
        }

        // fill data
        for (Object[] row : results) {
            String status = (String) row[0];
            float hours = ((Number) row[1]).floatValue() / 3600f; // seconds → hours
            map.put(status, hours);
        }

        return map;
    }

}
