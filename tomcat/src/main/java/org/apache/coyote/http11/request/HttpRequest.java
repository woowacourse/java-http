package org.apache.coyote.http11.request;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean containsQuery() {
        return requestLine.containsQuery();
    }

    public boolean containsQuery(final String key) {
        return requestLine.containsQuery(key);
    }

    public String getQueryParameter(final String queryKey) {
        return requestLine.getQueryParameter(queryKey);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public String getCookie(final String cookieName) {
        return headers.getCookie(cookieName);
    }

    public boolean containsBody(final String key) {
        return body.contains(key);
    }

    public String getBody(final String key) {
        return body.get(key);
    }

    public boolean containsCookie(final String cookieName) {
        return headers.containsCookie(cookieName);
    }
}
