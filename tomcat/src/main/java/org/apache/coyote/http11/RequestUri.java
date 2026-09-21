package org.apache.coyote.http11;

import java.util.Map;

public final class RequestUri {

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
        this.queryParameters = UrlEncodedParameters.parse(uri.substring(queryIndex + 1));
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }
}
