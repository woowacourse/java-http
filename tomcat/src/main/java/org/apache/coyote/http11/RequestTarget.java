package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestTarget {

    private static final int ENDPOINT_INDEX = 0;
    private static final int QUERY_INDEX = 1;
    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_DELIMITER_REGEX = "\\?";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    public static final int PARAM_KEY_INDEX = 0;
    public static final int PARAM_VALUE_INDEX = 1;

    private final String endPoint;
    private final Map<String, List<String>> queryStrings;

    public RequestTarget(String endPoint, Map<String, List<String>> queryStrings) {
        this.endPoint = endPoint;
        this.queryStrings = queryStrings;
    }

    public static RequestTarget from(String value) {
        if (value.contains(QUERY_DELIMITER)) {
            String[] values = value.split(QUERY_DELIMITER_REGEX);
            return new RequestTarget(values[ENDPOINT_INDEX], createQueryStrings(values[QUERY_INDEX]));
        }
        return new RequestTarget(value, Map.of());
    }

    private static Map<String, List<String>> createQueryStrings(String source) {
        Map<String, List<String>> queryStrings = new HashMap<>();

        for (String queryString : source.split(PARAM_DELIMITER)) {
            String[] query = queryString.split(KEY_VALUE_DELIMITER);
            queryStrings.computeIfAbsent(query[PARAM_KEY_INDEX], k -> new ArrayList<>())
                    .add(query[PARAM_VALUE_INDEX]);
        }

        return queryStrings;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public Map<String, List<String>> getQueryStrings() {
        return Collections.unmodifiableMap(queryStrings);
    }

    @Override
    public String toString() {
        String queryStrings = this.queryStrings.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));

        return "RequestTarget{" +
               "endPoint='" + endPoint + '\'' +
               ", queryStrings=" + queryStrings +
               '}';
    }
}
