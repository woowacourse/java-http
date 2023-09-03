package org.apache.coyote.http11;

public class HttpRequest {

    private final String startLine;

    public HttpRequest(String startLine) {
        this.startLine = startLine;
    }

    public String method() {
        return startLine.split(" ")[0];
    }

    public String uri() {
        String uri = startLine.split(" ")[1];
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf('?'));
        }
        return uri;
    }

    public String quiryString() {
        String uri = startLine.split(" ")[1];
        if (uri.contains("?")) {
            return uri.substring(uri.indexOf('?') + 1);
        }
        return null;
    }

    public String httpVersion() {
        return startLine.split(" ")[2];
    }
}
