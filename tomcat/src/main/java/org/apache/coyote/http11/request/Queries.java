package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Queries {

    public static final Queries EMPTY_QUERIES = new Queries(Map.of());
    private static final String QUERIES_SEPARATOR = "&";
    private static final String QUERY_SEPARATOR = "=";

    private final Map<String, String> values;

    public Queries(Map<String, String> values) {
        this.values = values;
    }

    public static Queries of(String queries) {
        List<String> splitQueries = Arrays.stream(queries.split(QUERIES_SEPARATOR))
                .filter(Queries::isValidQuery)
                .toList();
        Map<String, String> map = new HashMap<>();
        for (String query : splitQueries) {
            int index = query.indexOf(QUERY_SEPARATOR);
            String key = query.substring(0, index);
            String value = query.substring(index + 1);
            map.put(key, value);
        }
        return new Queries(map);
    }

    private static boolean isValidQuery(String query) {
        int index = query.indexOf(QUERY_SEPARATOR);
        int queryLastIndex = query.length() - 1;
        return index != -1 && index != queryLastIndex;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String get(String key) {
        return values.get(key);
    }

    public Set<String> getParameters() {
        return values.keySet();
    }
}
