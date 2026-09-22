package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {
    private final String statusLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(String statusLine, Map<String, String> headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(String statusLine, Map<String, String> headers) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = "";
    }

}
