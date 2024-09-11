package org.apache.coyote.http11.request;

import java.util.Arrays;
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
        String[] pathAndQuery = url.split(PATH_QUERY_DELIMITER, 2);
        if (isQueryPartExist(pathAndQuery)) {
            return queryParams;
        }

        String queryPart = pathAndQuery[QUERY_INDEX];
        Arrays.stream(queryPart.split(QUERY_PAIR_DELIMITER))
                .map(part -> part.split(QUERY_KEY_VALUE_DELIMITER, 2))
                .forEach(pair -> queryParams.put(pair[0], pair[1]));
        return queryParams;
    }

    private static boolean isQueryPartExist(String[] pathAndQuery) {
        return pathAndQuery.length != 2 || pathAndQuery[QUERY_INDEX].isEmpty();
    }
}
