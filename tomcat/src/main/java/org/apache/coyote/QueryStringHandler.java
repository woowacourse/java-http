package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class QueryStringHandler {

    public static Map<String, String> queryParams(String uri) {
        String[] uriPaths = uri.split("\\?");
        String queryString = uriPaths[1];
        String[] queryParams = queryString.split("&");
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParam : queryParams) {
            String[] splitQuery = queryParam.split("=");
            queryMap.put(splitQuery[0], splitQuery[1]);
        }
        return queryMap;
    }
}
