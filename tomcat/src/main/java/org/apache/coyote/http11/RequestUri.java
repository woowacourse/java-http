package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private final String uri;
    private final HashMap<String, String> queries = new HashMap<>();

    public RequestUri(String requestUri) {
        String[] uriSplit = requestUri.split("\\?");
        uri = uriSplit[0];
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
}
