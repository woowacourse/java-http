package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestMessage {

    private final String startLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequestMessage(
            final String startLine,
            final Map<String, String> headers,
            final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public String findHeaderValue(String headerTitle) {
        return headers.getOrDefault(headerTitle, null);
    }

    public boolean isNull() {
        return startLine == null || startLine.isBlank();
    }
    public String getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
