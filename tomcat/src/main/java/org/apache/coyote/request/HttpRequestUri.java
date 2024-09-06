package org.apache.coyote.request;

import org.apache.coyote.parsher.QueryParser;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpRequestUri {

    private static final int ENDPOINT_INDEX = 0;
    private static final int QUERY_INDEX = 1;
    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_DELIMITER_REGEX = "\\?";

    private final String endPoint;
    private final Map<String, List<String>> queryStrings;

    private HttpRequestUri(String endPoint, Map<String, List<String>> queryStrings) {
        this.endPoint = endPoint;
        this.queryStrings = queryStrings;
    }

    public static HttpRequestUri from(String value) {
        if (value.contains(QUERY_DELIMITER)) {
            String[] values = value.split(QUERY_DELIMITER_REGEX);
            return new HttpRequestUri(values[ENDPOINT_INDEX], QueryParser.parse(values[QUERY_INDEX]));
        }
        return new HttpRequestUri(value, Map.of());
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
}
