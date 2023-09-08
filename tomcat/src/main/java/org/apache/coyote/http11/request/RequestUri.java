package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private static final String PERIOD = ".";
    private static final String HTML_EXTENSION = ".html";

    private final String uri;
    private final HashMap<String, String> queries = new HashMap<>();

    public RequestUri(String requestUri) {
        String[] uriSplit = requestUri.split("\\?");
        uri = resolveExtension(uriSplit[0]);

        if (uriSplit.length > 1) {
            String[] queriesSplit = uriSplit[1].split("&");
            for (String query : queriesSplit) {
                String[] keyValue = query.split("=");
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
        if (uri.contains(PERIOD)) {
            return uri;
        }
        return uri + HTML_EXTENSION;
    }
}
