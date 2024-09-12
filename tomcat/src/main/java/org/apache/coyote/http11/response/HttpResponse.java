package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Headers;

public class HttpResponse {

    private static final String BLANK_LINE_CONTENT = "";

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final StatusLine statusLine, final Headers headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String format() {
        return String.join("\r\n", statusLine.format(), headers.format(), BLANK_LINE_CONTENT, body);
    }
}
