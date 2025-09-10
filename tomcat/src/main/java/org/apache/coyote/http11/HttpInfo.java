package org.apache.coyote.http11;

public class HttpInfo {

    private final String method;
    private final String path;
    private final String protocol;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpInfo(String line) {
        String[] lines = line.split(" ");
        validate(lines);

        this.method = lines[0];
        this.path = lines[1];
        this.protocol = lines[2];
    }

    private void validate(String[] lines) {
        if (lines.length != 3) {
            throw new IllegalArgumentException("유효하지 않은 HTTP 첫번째 라인입니다.");
        }
    }
}
