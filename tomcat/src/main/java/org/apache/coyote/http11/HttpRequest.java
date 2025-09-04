package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String path;
    private final Map<String, String> queryParams;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final String[] requestParts = requestLine.split(" ");
        final String uri = requestParts[1];

        if (uri.contains("?")) {
            int queryIndex = uri.indexOf("?");
            this.path = uri.substring(0, queryIndex);
            this.queryParams = parseQueryString(uri.substring(queryIndex + 1));
            return;
        }

        this.path = uri;
        this.queryParams = Collections.emptyMap();
    }

    private Map<String, String> parseQueryString(String queryString) {
        final Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            final String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }
}
