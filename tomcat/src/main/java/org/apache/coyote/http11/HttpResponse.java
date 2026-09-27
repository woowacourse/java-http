package org.apache.coyote.http11;

public class HttpResponse {

    private StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    private HttpResponse() {
        this.statusLine = null;
        this.headers = HttpHeaders.empty();
        this.body = "";
    }

    public static HttpResponse init() {
        return new HttpResponse();
    }

    public void sendRedirect(final String redirectPath) {
        addHeader("Location", redirectPath);
        addHeader("Content-Length", String.valueOf(0));
    }

    public boolean hasStatusLine() {
        return statusLine != null;
    }

    public void addStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, String.valueOf(value));
    }

    public void addBody(final String body) {
        this.body = body;
    }

    public String getMessage() {
        if (body.isEmpty()) {
            return String.join("\r\n",
                statusLine.toString(),
                headers.toString());
        }
        return String.join("\r\n",
            statusLine.toString(),
            headers.toString(),
            "",
            body);
    }
}
