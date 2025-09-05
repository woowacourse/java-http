package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private final String method;
    private final String uri;

    public HttpRequest(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public Map<String, String> getQueryStrings() {
        Map<String, String> queryStrings = new HashMap<>();

        Optional<String> queryString = getQueryString();
        if (queryString.isEmpty()) {
            return new HashMap<>();
        }

        for (String keyValue : queryString.get().split("&")) {
            String[] keyValues = keyValue.split("=");
            queryStrings.put(keyValues[0], keyValues[1]);
        }

        return queryStrings;
    }

    private Optional<String> getQueryString() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return Optional.empty();
        }

        return Optional.of(uri.substring(index + 1));
    }

    public String getPath() {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
