package org.apache.coyote.response;

public class HttpResponse {


    private final HttpResponseStartLine startLine;
    private final HttpResponseHeaders headers;
    private String body;

    private HttpResponse(HttpResponseStartLine startLine, HttpResponseHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create() {
        HttpResponseStartLine startLine = HttpResponseStartLine.defaultLine();
        return new HttpResponse(startLine, new HttpResponseHeaders(), null);
    }

    public void addContentType(String accept) {
        addHeader("Content-Type", accept + ";charset=utf-8");
    }

    public void addBody(String body) {
        this.body = body;
        addHeader("Content-Length", String.valueOf(body.getBytes().length));
    }

    private void addHeader(String key, String value) {
        headers.add(key, value);
    }

    @Override
    public String toString() {
        if (body == null) {
            return String.join(
                    "\r\n",
                    startLine.toString(),
                    headers.toString(),
                    ""
            );
        }
        return String.join(
                "\r\n",
                startLine.toString(),
                headers.toString(),
                "",
                body
        );
    }
}
