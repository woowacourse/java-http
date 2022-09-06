package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.request.header.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final List<String> body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final List<String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final String requestLine, final List<String> headers, final List<String> body) {
        return new HttpRequest(RequestLine.from(requestLine), HttpHeaders.from(headers), body);
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueries();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}
