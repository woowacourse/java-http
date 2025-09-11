package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final byte[] body;
    private final HttpCookie cookies;

    public HttpRequest(
            final RequestLine requestLine,
            final HttpHeaders headers,
            final byte[] body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.cookies = HttpCookie.fromHeader(headers.getCookieHeader());
    }

    public String getQueryString() {
        final String uri = requestLine.getPath();
        if (!uri.contains("?")) {
            return "";
        }
        final String[] split = uri.split("\\?");
        return split.length > 1 ? split[1] : "";
    }

    public String getQueryString() {
        final String uri = requestLine.getPath();
        if (!uri.contains("?")) {
            return "";
        }
        final String[] split = uri.split("\\?");
        return split.length > 1 ? split[1] : "";
    }

    public String getBodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }
}
