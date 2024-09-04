package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.util.QueryStringParser;

public class Http11RequestTarget {

    private static final int ENDPOINT_INDEX = 0;
    private static final int QUERY_INDEX = 1;
    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_DELIMITER_REGEX = "\\?";

    private final String endPoint;
    private final Map<String, List<String>> queryStrings;

    private Http11RequestTarget(String endPoint, Map<String, List<String>> queryStrings) {
        this.endPoint = endPoint;
        this.queryStrings = queryStrings;
    }

    public static Http11RequestTarget from(String value) {
        if (value.contains(QUERY_DELIMITER)) {
            String[] values = value.split(QUERY_DELIMITER_REGEX);
            return new Http11RequestTarget(values[ENDPOINT_INDEX], QueryStringParser.parseQueryString(values[QUERY_INDEX]));
        }
        return new Http11RequestTarget(value, Map.of());
    }

    public boolean hasParam(String key) {
        return queryStrings.containsKey(key);
    }

    public String getParam(String key) {
        if (hasParam(key)) {
            return queryStrings.get(key).get(0);
        }
        return "";
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
