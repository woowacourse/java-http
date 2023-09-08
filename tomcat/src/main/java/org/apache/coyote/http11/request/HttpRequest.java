package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final String requestLine, final Map<String, String> headers, final String body) {
        return new HttpRequest(
                RequestLine.from(requestLine),
                headers,
                body
        );
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getQueryStrings() {
        return requestLine.getQueryStrings();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
