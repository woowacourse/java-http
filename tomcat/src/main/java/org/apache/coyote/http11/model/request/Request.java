package org.apache.coyote.http11.model.request;

import static org.apache.coyote.http11.model.StringFormat.CRLF;

import java.util.Map;

public class Request {

    private static final int START_LINE_INDEX = 0;

    private final RequestLine startLine;

    private Request(final String requestLine) {
        this.startLine = new RequestLine(requestLine);
    }

    public static Request from(final String request) {
        String[] lines = request.split(CRLF);
        return new Request(lines[START_LINE_INDEX]);
    }

    public String getUrl() {
        return startLine.getUrl();
    }

    public Map<String, String> getQueryParams() {
        return startLine.getQueryParams();
    }
}
