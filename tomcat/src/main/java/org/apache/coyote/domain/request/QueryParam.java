package org.apache.coyote.domain.request;

import java.util.HashMap;
import java.util.Map;

public class QueryParam {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_STRING_REGEX = "&";
    private static final String QUERY_MAP_DELIMITER = "=";

    private final Map<String, String> queryParam;

    private QueryParam(Map<String, String> queryParam) {
        this.queryParam = queryParam;
    }

    public static QueryParam from(String uri) {
        Map<String, String> queryMap = new HashMap<>();
        if (!uri.contains(QUERY_STRING_DELIMITER)) {
            return new QueryParam(queryMap);
        }
        String[] uriPaths = uri.split("\\?");
        String queryString = uriPaths[1];
        String[] queryParams = queryString.split(QUERY_STRING_REGEX);
        for (String queryParam : queryParams) {
            String[] splitQuery = queryParam.split(QUERY_MAP_DELIMITER);
            queryMap.put(splitQuery[0], splitQuery[1]);
        }
        return new QueryParam(queryMap);
    }

    public String getQueryValue(String key) {
        return queryParam.get(key);
    }

    public boolean isEmpty(){
        return queryParam.isEmpty();
    }
}
