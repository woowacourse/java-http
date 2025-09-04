package org.apache.coyote.http11;

public class HttpRequest {

    private final String resourcePath;
    private final QueryParameters queryParameters;

    public HttpRequest(String resourcePath, QueryParameters queryParameters) {
        this.resourcePath = resourcePath;
        this.queryParameters = queryParameters;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getQueryParameter(String key) {
        return queryParameters.getParameter(key);
    }
}
