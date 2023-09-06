package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String JSESSION_ID = "JSESSIONID";

    private final HttpHeaders headers;
    private final HttpMethod method;
    private final HttpRequestURI requestURI;
    private final String protocol;
    private final Map<String, String> body;

    public HttpRequest(
        final HttpHeaders headers,
        final HttpMethod httpMethod,
        final HttpRequestURI requestURI,
        final String protocol,
        final Map<String, String> body) {
        this.headers = headers;
        this.method = httpMethod;
        this.requestURI = requestURI;
        this.protocol = protocol;
        this.body = body;
    }

    public boolean containsAccept(final String contentType) {
        return headers.containsHeaderNameAndValue(HttpHeaderName.ACCEPT, contentType);
    }

    public boolean hasQueryString() {
        return requestURI.hasQueryString();
    }

    public boolean containsContentType(final String value) {
        return headers.containsHeaderNameAndValue(HttpHeaderName.CONTENT_TYPE, value);
    }

    public boolean containsCookieAndJSessionID() {
        return headers.containsHeaderNameAndValue(HttpHeaderName.COOKIE, JSESSION_ID);
    }

    public boolean isSameMethod(final HttpMethod httpMethod) {
        return method == httpMethod;
    }

    public HttpCookie getCookie() {
        final String rawCookie = headers.getHeaderValue(HttpHeaderName.COOKIE);
        return HttpCookie.from(rawCookie);
    }

    public String getPath() {
        return requestURI.getPath();
    }

    public QueryStrings getQueryStrings() {
        return requestURI.getQueryStrings();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpRequestURI getRequestURI() {
        return requestURI;
    }

    public Map<String, String> getBody() {
        return new HashMap<>(body);
    }
}
