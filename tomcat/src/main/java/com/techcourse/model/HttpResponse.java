package com.techcourse.model;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final StatusLine statusLine;
    private final Header headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, Header headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String toResponse() {
        return String.join(CRLF,
                statusLine.toResponse().concat(headers.toResponse()),
                "", body);
    }
}
