package org.apache.coyote.http11;

public class HttpResponse {

    private StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    public HttpResponse() {
        this(StatusLine.ok(), HttpHeaders.empty(), "");
    }

    private HttpResponse(final StatusLine statusLine, final HttpHeaders headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(
            String contentType,
            String body) {
        HttpHeaders headers = HttpHeaders.empty();
        headers.add("Content-Type", contentType);
        headers.add("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusLine.ok(), headers, body);
    }

    public static HttpResponse notFound(String body) {
        HttpHeaders headers = HttpHeaders.empty();
        headers.add("Content-Type", "text/html;charset=utf-8");
        headers.add("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(StatusLine.notFound(), headers, body);
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void redirectTo(String path) {
        statusLine = StatusLine.redirect();
        headers.add("Location", path);
        setBody("");
    }

    public void notFound() {
        statusLine = StatusLine.notFound();
    }

    public void setBody(String body) {
        this.body = body;
        headers.add("Content-Length", String.valueOf(body.getBytes().length));
    }

    @Override
    public String toString() {
        return statusLine + "\r\n" + headers + "\r\n\r\n" + body;
    }
}
