package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private static final String PERIOD = ".";
    private static final String HTML_EXTENSION = ".html";
    private static final String MAIN_PAGE = "/";
    private static final String AMPERSAND = "&";
    private static final String EQUAL_SIGN = "=";
    private static final String QUERY_SEPARATOR = "\\?";
    private static final int QUERY_EXISTENCE_SIZE = 1;

    private final String uri;
    private final HashMap<String, String> queries = new HashMap<>();

    public RequestUri(String requestUri) {
        String[] uriSplit = requestUri.split(QUERY_SEPARATOR);
        uri = resolveExtension(uriSplit[0]);

        if (uriSplit.length > QUERY_EXISTENCE_SIZE) {
            String[] queriesSplit = uriSplit[1].split(AMPERSAND);
            for (String query : queriesSplit) {
                String[] keyValue = query.split(EQUAL_SIGN);
                queries.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueries() {
        return new HashMap<>(queries);
    }

    private String resolveExtension(String uri) {
        if (uri.contains(PERIOD) || uri.equals(MAIN_PAGE)) {
            return uri;
        }

        return uri + HTML_EXTENSION;
    }
}
