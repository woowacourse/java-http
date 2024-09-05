package org.apache.coyote.http11.response;

public class Http11Response {

    private Http11ResponseStartLine startLine;
    private Http11ResponseHeaders headers;
    private String body;

    private Http11Response(Http11ResponseStartLine startLine, Http11ResponseHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Response create() {
        Http11ResponseStartLine startLine = Http11ResponseStartLine.defaultLine();
        return new Http11Response(startLine, new Http11ResponseHeaders(), null);
    }

    public void sendRedirect(String url) {
        this.startLine = new Http11ResponseStartLine(HttpStatusCode.FOUND);
        addHeader("Location", url);
    }

    public void addCookie(String key, String value) {
        addHeader("Set-Cookie", key + "=" + value);
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
