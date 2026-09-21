package org.apache.coyote.http11;

import java.util.List;

public final class RequestUri {

    private final String path;
    private final HttpParameters queryParameters;

    public RequestUri(String uri) {
        int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            this.path = uri;
            this.queryParameters = HttpParameters.empty();
            return;
        }

        this.path = uri.substring(0, queryIndex);
        this.queryParameters = HttpParameters.parse(uri.substring(queryIndex + 1));
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String name) {
        return queryParameters.getFirst(name);
    }

    List<String> getQueryParameterValues(String name) {
        return queryParameters.getAll(name);
    }
}
