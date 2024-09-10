package org.apache.coyote.http11;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    public HttpResponse(StatusLine statusLine, HttpHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(StatusLine statusLine, HttpHeaders headers) {
        this.statusLine = statusLine;
        this.headers = headers;
    }

    public String build() {
        return statusLine + "\r\n" + headers + "\r\n" + body;
    }

    @Override
    public String toString() {
        return build();
    }
}
