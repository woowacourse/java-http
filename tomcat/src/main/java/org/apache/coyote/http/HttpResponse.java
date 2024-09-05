package org.apache.coyote.http;

import static org.apache.coyote.http.Constants.CRLF;

public class HttpResponse {

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
