package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public record HttpURL(String fullUrl, String path, Map<String, String> queryParameters) {

    private static final String PATH_QUERY_DELIMITER = "\\?";
    private static final int PATH_INDEX = 0;
    private static final int QUERY_INDEX = 1;
    private static final String QUERY_PAIR_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    public static HttpURL from(String fullUrl) {
        return new HttpURL(fullUrl, parsePath(fullUrl), parseQueryParameters(fullUrl));
    }

    private static String parsePath(String url) {
        return url.split(PATH_QUERY_DELIMITER)[PATH_INDEX];
    }

    private static Map<String, String> parseQueryParameters(String url) {
        Map<String, String> queryParams = new HashMap<>();
        String[] pathAndQuery = url.split(PATH_QUERY_DELIMITER);
        if (pathAndQuery.length != 2) {
            return queryParams;
        }

        String queryPart = pathAndQuery[QUERY_INDEX];
        String[] pairs = queryPart.split(QUERY_PAIR_DELIMITER);
        for (String pair : pairs) {
            String[] keyValue = pair.split(QUERY_KEY_VALUE_DELIMITER);
            queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }
}
