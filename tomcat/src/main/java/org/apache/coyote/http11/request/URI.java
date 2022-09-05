package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class URI {

    private static final String PATH_SPLITTER = "?";
    private static final String QUERY_STRING_KEY_VALUE_SEPARATOR = "=";
    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final int QUERY_STRING_KEY_INDEX = 0;
    private static final int QUERY_STRING_VALUE_INDEX = 1;

    private final String path;
    private final Map<String, String> queryParams = new HashMap<>();

    public URI(String uri) {
        this.path = getPath(uri);
        parseToQuery(uri);
    }

    private String getPath(String uri) {
        if (uri.contains(PATH_SPLITTER)) {
            int index = uri.indexOf(PATH_SPLITTER);
            return uri.substring(0, index);
        }
        return uri;
    }

    private void parseToQuery(String uri) {
        if (uri.contains(PATH_SPLITTER)) {
            int index = uri.indexOf(PATH_SPLITTER);
            String queryString = uri.substring(index + 1);
            String[] queries = queryString.split(QUERY_STRING_SEPARATOR);
            for (String query : queries) {
                String[] s = query.split(QUERY_STRING_KEY_VALUE_SEPARATOR);
                queryParams.put(s[QUERY_STRING_KEY_INDEX], s[QUERY_STRING_VALUE_INDEX]);
            }
        }
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return Map.copyOf(queryParams);
    }
}
