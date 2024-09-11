package org.apache.coyote.http11.request;

public class RequestLine {

    private static final int REQUEST_LINE_ELEMENTS_COUNT = 3;

    private final String method;
    private final String path;
    private final String version;

    public RequestLine(String requestLine) {
        String[] split = requestLine.split(" ");
        if (split.length != REQUEST_LINE_ELEMENTS_COUNT) {
            throw new IllegalArgumentException("requestLine의 형식이 올바르지 않습니다. requestLine = " + requestLine);
        }
        this.method = split[0];
        this.path = split[1];
        this.version = split[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
