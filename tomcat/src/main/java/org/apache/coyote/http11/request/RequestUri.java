package org.apache.coyote.http11.request;

import java.util.Optional;

public class RequestUri {

    private final String path;
    private final QueryParameters queryParameters;

    public RequestUri(String uri) {
        int queryIndex = uri.indexOf("?");
        if (queryIndex == -1) {
            this.path = uri;
            this.queryParameters = QueryParameters.from(null);
            return;
        }

        this.path = uri.substring(0, queryIndex);
        this.queryParameters = QueryParameters.from(uri.substring(queryIndex + 1));
    }

    public String getPath() {
        return path;
    }

    public String getResourcePath() {
        if (path.equals("/")) {
            return "/index.html";
        }

        if (!path.contains(".")) {
            return path + ".html";
        }

        return path;
    }

    public Optional<String> findParameter(String name) {
        return queryParameters.find(name);
    }
}
