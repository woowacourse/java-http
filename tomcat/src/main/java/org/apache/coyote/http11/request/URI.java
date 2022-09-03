package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class URI {

    private final String path;
    private final Map<String, String> queryParams = new HashMap<>();

    public URI(String uri) {
        this.path = getPath(uri);
        parseToQuery(uri);
    }

    private String getPath(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
    }

    private void parseToQuery(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            String queryString = uri.substring(index + 1);
            String[] queries = queryString.split("&");
            for (String query : queries) {
                String[] s = query.split("=");
                queryParams.put(s[0], s[1]);
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