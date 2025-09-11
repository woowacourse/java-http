package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;

public final class Http11Request {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    public Http11Request(
            final RequestLine requestLine,
            final HttpHeaders headers,
            final String body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Request createInvalid() {
        return new Http11Request(
                RequestLine.createInvalid(),
                new HttpHeaders(Map.of()),
                ""
        );
    }

    public boolean isPost() {
        return getMethod() == HttpMethod.POST;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, List<String>> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public Map<String, List<String>> getHeaders() {
        return headers.getHeaders();
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public String getBody() {
        return body;
    }
}
