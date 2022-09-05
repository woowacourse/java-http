package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class UriParser {

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
        Map<String, String> queryValues = new HashMap<>();
        int index = uri.indexOf(URL_QUERY_SEPARATOR);

        if (index == -1) {
            return queryValues;
        }

        String queryString = uri.substring(index + 1);

        String[] queries = queryString.split(QUERY_SEPARATOR);
        for (String query : queries) {
            String[] split = query.split(KEY_VALUE_SEPARATOR);
            queryValues.put(split[PARAMETER_NAME], split[PARAMETER_VALUE]);
        }
        return queryValues;
    }
}
