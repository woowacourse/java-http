package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.Headers;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final StatusLine statusLine, final Headers headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(final StatusLine statusLine, final Headers headers, final String body) {
        return new HttpResponse(statusLine, headers, body);
    }

    @Override
    public String toString() {
        return String.join("\r\n", statusLine.toString(),
                headers.toString(),
                "",
                body);
    }
}
