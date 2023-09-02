package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final HttpRequestStartLine httpRequestStartLine;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> queryParams;
    private final Map<String, String> payload;
    private final HttpRequestCookie cookie;

    public HttpRequest(
            final HttpRequestStartLine httpRequestStartLine,
            final Map<String, String> httpRequestHeaders,
            final Map<String, String> payload
    ) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.queryParams = httpRequestStartLine.getQueryParams();
        this.payload = payload;
        this.cookie = HttpRequestCookie.from(httpRequestHeaders.get("Cookie"));
    }

    public static HttpRequest of(final HttpRequestStartLine httpRequestStartLine, final Map<String, String> headers) {
        return new HttpRequest(
                httpRequestStartLine,
                headers,
                Map.of()
        );
    }

    public static HttpRequest of(
            final HttpRequestStartLine httpRequestStartLine,
            final Map<String, String> headers,
            final Map<String, String> payload
    ) {
        return new HttpRequest(
                httpRequestStartLine,
                headers,
                payload
        );
    }

    public HttpRequestStartLine getHttpStartLine() {
        return httpRequestStartLine;
    }

    public String getHeader(final String header) {
        return httpRequestHeaders.get(header);
    }

    public String getParam(final String parameter) {
        return queryParams.get(parameter);
    }

    public String getPayloadValue(final String key) {
        return payload.get(key);
    }

    public String getCookie(final String cookieKey) {
        return cookie.getCookieValue(cookieKey);
    }
}
