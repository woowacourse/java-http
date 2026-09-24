package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String version;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(" ");
        method = parts[0].trim();
        uri = parts[1].trim();
        version = parts[2].trim();
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
