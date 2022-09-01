package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestMessage {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public HttpRequestMessage(final String startLine) {
        this(startLine, new HashMap<>());
    }

    public HttpRequestMessage(final String startLine, final Map<String, String> headers) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = headers;
    }

    public void addHeader(final String key, final String value) {
        this.headers.put(key, value);
    }

    public String getRequestLine() {
        return requestLine.getHttpMethod() + " " + requestLine.getRequestTarget() + " " + requestLine.getHttpVersion();
    }

    public String getRequestTarget() {
        return requestLine.getRequestTarget();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
