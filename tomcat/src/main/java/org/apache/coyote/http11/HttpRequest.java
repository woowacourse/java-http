package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String EMPTY_VALUE = "";

    private final HttpRequestLine requestLine;
    private final QueryStrings queryStrings;
    private final HttpHeaders headers;
    private final HttpCookies cookies;
    private final Map<String, Object> requestBody;

    public HttpRequest(final HttpRequestLine requestLine, final QueryStrings queryStrings, final HttpHeaders headers,
                       final HttpCookies cookies, final Map<String, Object> requestBody) {
        this.requestLine = requestLine;
        this.queryStrings = queryStrings;
        this.headers = headers;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getContentType() {
        return headers.getContentType();
    }

    public String getQueryString(final String key) {
        final Map<String, String> values = queryStrings.getValues();
        return values.getOrDefault(key, EMPTY_VALUE);
    }

    public Map<String, Object> getRequestBody() {
        return requestBody;
    }

    public String getCookie(final String key) {
        return cookies.get(key);
    }
}
