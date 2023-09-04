package org.apache.coyote.response;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpResponseHeader headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, HttpResponseHeader headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String getResponse() {
        return statusLine.getStatusLine() + "\r\n" + headers.getResponseHeader() + "\r\n" + body;
    }
}
