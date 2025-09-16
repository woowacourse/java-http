package org.apache.coyote.http11;

public class RequestLine {
    private final String method;
    private final String path;
    private final String version;

    public RequestLine(String line) {
        final String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException();
        }
        this.method = parts[0];
        this.path = parts[1];
        this.version = parts[2];
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
