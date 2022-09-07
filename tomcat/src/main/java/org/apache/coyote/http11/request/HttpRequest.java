package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.common.HttpBody;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.request.header.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final RequestLine requestLine, final HttpHeaders headers, final HttpBody body) {
        return new HttpRequest(requestLine, headers, body);
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

    public Map<String, String> getBodies() {
        return body.getValue();
    }
}
