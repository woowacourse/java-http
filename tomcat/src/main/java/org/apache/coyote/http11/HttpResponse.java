package org.apache.coyote.http11;

public class HttpResponse {

    private StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;
    private String forwardPath;

    private HttpResponse() {
        this.statusLine = null;
        this.headers = HttpHeaders.empty();
        this.body = "";
        this.forwardPath = null;
    }

    public static HttpResponse init() {
        return new HttpResponse();
    }

    public String forwardPath() {
        return forwardPath;
    }

    public boolean hasStatusLine() {
        return statusLine != null;
    }

    public void addStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void addHeader(final String key, final Object value) {
        headers.put(key, String.valueOf(value));
    }

    public void addBody(final String body) {
        this.body = body;
    }

    public void sendRedirect(final String url) {
        addHeader("Location", url);
        addHeader("Content-Length", 0);
    }

    public boolean hasForwardPath() {
        return forwardPath != null;
    }

    public void forward(final String forwardPath) {
        this.forwardPath = forwardPath;
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
