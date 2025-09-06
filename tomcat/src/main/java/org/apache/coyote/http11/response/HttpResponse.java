package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(Map<String, String> headers, String body) {
        this(new StatusLine(), headers, body);
    }

    public HttpResponse(Map<String, String> headers) {
        this(new StatusLine(), headers, null);
    }

    @Override
    public String toString() { // TODO 2025. 9. 6. 19:26: header와 body 사이 공백
        return String.join("\r\n",
                statusLine.toString(),
                headers.toString(),
                body);
    }
}
