package org.apache.coyote.http11.request;

public class RequestUri {

    private final String path;
    private final Parameters parameters;

    public RequestUri(final String requestUri) {
        final int queryIndex = requestUri.indexOf('?');

        if (queryIndex < 0) {
            this.path = requestUri;
            this.parameters = new Parameters();
            return;
        }

        this.path = requestUri.substring(0, queryIndex);
        this.parameters = new Parameters(requestUri.substring(queryIndex + 1));
    }

    public String getPath() {
        return path;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }
}
