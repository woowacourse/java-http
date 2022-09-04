package org.apache.coyote.handler;

import java.util.HashMap;
import java.util.Map;

public class QueryStringHandler {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_STRING_REGEX = "&";
    private static final String QUERY_MAP_DELIMITER = "=";

    public static Map<String, String> queryParams(String uri) {
        Map<String, String> queryMap = new HashMap<>();
        if (!uri.contains(QUERY_STRING_DELIMITER)) {
            return queryMap;
        }
        String[] uriPaths = uri.split("\\?");
        String queryString = uriPaths[1];
        String[] queryParams = queryString.split(QUERY_STRING_REGEX);
        for (String queryParam : queryParams) {
            String[] splitQuery = queryParam.split(QUERY_MAP_DELIMITER);
            queryMap.put(splitQuery[0], splitQuery[1]);
        }
        return queryMap;
    }
}
