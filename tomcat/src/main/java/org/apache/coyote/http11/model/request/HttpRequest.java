package org.apache.coyote.http11.model.request;

import static org.apache.coyote.http11.model.StringFormat.CRLF;

import java.util.Map;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;

    private final RequestLine startLine;

    private HttpRequest(final String requestLine) {
        this.startLine = new RequestLine(requestLine);
    }

    public static HttpRequest from(final String request) {
        String[] lines = request.split(CRLF);
        return new HttpRequest(lines[START_LINE_INDEX]);
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public Map<String, String> getQueryParams() {
        return startLine.getQueryParams();
    }
}
