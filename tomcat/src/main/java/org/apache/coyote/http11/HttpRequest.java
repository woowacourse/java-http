package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryString;
    private final String protocolVersion;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String body;

    public HttpRequest(String method, String path, Map<String, String> queryString, String protocolVersion,
                       Map<String, String> headers, HttpCookie httpCookie, String body) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
        this.httpCookie = httpCookie;
        this.body = body;
    }

    public boolean hasQueryString() {
        return !queryString.isEmpty();
    }

    public boolean hasCookieFrom(String input) {
        return httpCookie.hasValue(input);
    }

    public String getQueryData(String input) {
        return queryString.get(input);
    }

    public String getHeaderData(String input) {
        return headers.get(input);
    }

    public String getPath() {
        return this.path;
    }

    public String getMethod() {
        return this.method;
    }

    public String getBody() {
        return body;
    }
}
