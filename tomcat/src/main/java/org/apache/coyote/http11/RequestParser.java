package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final String URL_QUERY_SEPARATOR = "?";
    private static final String QUERY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int PARAMETER_NAME = 0;
    private static final int PARAMETER_VALUE = 1;

    public static String extractUrl(String uri) {
        int index = uri.indexOf(URL_QUERY_SEPARATOR);
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    public static Map<String, String> parseUri(String uri) {
        int index = uri.indexOf(URL_QUERY_SEPARATOR);

        if (index == -1) {
            return new HashMap<>();
        }

        String queryString = uri.substring(index + 1);
        return parseQueryString(queryString);
    }

    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parsedData = new HashMap<>();

        if (queryString == null) {
            return parsedData;
        }

        String[] queries = queryString.split(QUERY_SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(KEY_VALUE_SEPARATOR);
            parsedData.put(split[PARAMETER_NAME], split[PARAMETER_VALUE]);
        }
        return parsedData;
    }
}
