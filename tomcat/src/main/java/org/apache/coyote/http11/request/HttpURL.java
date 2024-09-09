package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public record HttpURL(String fullUrl, String path, Map<String, String> queryParameters) {

    private static final String QUERY_DELIMITER = "\\?";
    private static final String QUERY_PAIR_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    public static HttpURL from(String fullUrl) {
        return new HttpURL(fullUrl, parsePath(fullUrl), parseQueryParameters(fullUrl));
    }

    private static String parsePath(String url) {
        return url.split(QUERY_DELIMITER)[0];
    }
    private static Map<String, String> parseQueryParameters(String url) {
        Map<String, String> queryParams = new HashMap<>();
        String[] split = url.split(QUERY_DELIMITER);
        if (split.length != 2) {
            return queryParams;
        }

        String queryPart = split[1];
        String[] pairs = queryPart.split(QUERY_PAIR_DELIMITER);
        for (String pair : pairs) {
            String[] keyValue = pair.split(QUERY_KEY_VALUE_DELIMITER);
            queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }
}
