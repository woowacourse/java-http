package org.apache.catalina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper {

    public static Map<String, String> toMap(String query) {
        return toMap(query, "&", "=");
    }

    public static Map<String, String> toMap(String query, String delimiter, String mappingDelimiter) {
        if (query == null || query.isBlank()) {
            return Map.of();
        }
        Map<String, String> params = new HashMap<>();
        String[] splits = query.split(delimiter);
        for (String split : splits) {
            String[] keyValue = split.split(mappingDelimiter);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }

    public static Map<String, String> toMap(List<String> queries, String mappingDelimiter) {
        Map<String, String> params = new HashMap<>();
        for (String query : queries) {
            String[] keyValue = query.split(mappingDelimiter);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }
}
