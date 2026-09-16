package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {

    private final String path;
    private final Map<String, String> queryParameters;

    public RequestUri(String uri) {
        int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            this.path = uri;
            this.queryParameters = Map.of();
            return;
        }

        this.path = uri.substring(0, queryIndex);
        this.queryParameters = parseQueryString(uri.substring(queryIndex + 1));
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        for (String query : queryString.split("&")) {
            String[] pair = query.split("=", 2);

            if (pair.length == 2) {
                parameters.put(pair[0], pair[1]);
            }
        }

        return parameters;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }
}
