package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String resourcePath;
    private final QueryParameters queryParameters;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String resourcePath, QueryParameters queryParameters, Map<String, String> headers, String body) {
        this.resourcePath = resourcePath;
        this.queryParameters = queryParameters;
        this.headers = headers;
        this.body = body;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getQueryParameter(String key) {
        return queryParameters.getParameter(key);
    }

    public boolean hasQueryParameter() {
        return queryParameters.hasAnyParameter();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Cookies getCookies() {
        return new Cookies(getHeader("Cookie"));
    }
}
