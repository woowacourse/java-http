package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.request.header.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpRequestBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final HttpRequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final RequestLine requestLine, final HttpHeaders headers, final HttpRequestBody body) {
        return new HttpRequest(requestLine, headers, body);
    }

    public boolean doesNotHaveSessionId() {
        return !headers.hasSessionId();
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getSessionId() {
        return headers.getSessionId();
    }

    public Map<String, String> getBodies() {
        return body.getValue();
    }
}
