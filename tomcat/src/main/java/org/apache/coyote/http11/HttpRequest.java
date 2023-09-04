package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final Map<String, String> body;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> header, final Map<String, String> body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final RequestLine requestLine, final Map<String, String> header, final Map<String, String> body) {
        return new HttpRequest(requestLine, header, body);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getTarget() {
        return requestLine.getTarget();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
