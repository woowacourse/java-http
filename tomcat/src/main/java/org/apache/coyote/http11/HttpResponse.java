package org.apache.coyote.http11;

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
}
