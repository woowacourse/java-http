package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Query {

    private static final String QUERY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> queries;

    private Query(Map<String, String> queries) {
        this.queries = queries;
    }

    public static Query create(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return new Query(new HashMap<>());
        }
        Map<String, String> queryMap = new HashMap<>();
        Arrays.stream(queryString.split(QUERY_DELIMITER))
                .forEach(eachQuery -> {
                    String[] keyAndValue = eachQuery.split(KEY_VALUE_DELIMITER);
                    queryMap.put(keyAndValue[0], keyAndValue[1]);
                });
        return new Query(queryMap);
    }

    public boolean containsKey(String queryName) {
        return queries.containsKey(queryName);
    }

    public String get(String queryName) {
        return queries.get(queryName);
    }

    public boolean isEmpty() {
        return queries.isEmpty();
    }
}
