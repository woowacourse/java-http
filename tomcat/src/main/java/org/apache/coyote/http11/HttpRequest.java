package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String EMPTY_VALUE = "";

    private final HttpMethod method;
    private final String requestUri;
    private final QueryStrings queryStrings;
    private final HttpHeaders headers;
    private final HttpCookies cookies;
    private final Map<String, String> requestBody;

    public HttpRequest(final HttpMethod method, final String requestUri, final QueryStrings queryStrings,
                       final HttpHeaders headers, final HttpCookies cookies, final Map<String, String> requestBody) {
        this.method = method;
        this.requestUri = requestUri;
        this.queryStrings = queryStrings;
        this.headers = headers;
        this.cookies = cookies;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getContentType() {
        return headers.getContentType();
    }

    public String getQueryString(final String key) {
        final Map<String, String> values = queryStrings.getValues();
        return values.getOrDefault(key, EMPTY_VALUE);
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    public String getCookie(final String key) {
        return cookies.get(key);
    }
}
