package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String httpVersion;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(" ");
        this.method = parts[0];
        this.httpVersion = parts[2];

        String requestUri = parts[1];
        int index = requestUri.indexOf("?");
        if (index == -1) {
            this.path = requestUri;
            this.queryString = "";
            return;
        }
        this.path = requestUri.substring(0, index);
        this.queryString = requestUri.substring(index + 1);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
