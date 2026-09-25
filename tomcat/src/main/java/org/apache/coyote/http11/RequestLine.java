package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(" ");
        String uri = parts[1];
        String path = uri;
        int index = uri.indexOf("?");
        if (index != -1) {
            path = uri.substring(0, index);
        }
        this.method = parts[0];
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
