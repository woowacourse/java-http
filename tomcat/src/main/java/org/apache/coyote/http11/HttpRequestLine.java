package org.apache.coyote.http11;

public class HttpRequestLine {

    private final String method;
    private final String fullPath;
    private final String protocolVersion;

    public HttpRequestLine(String method, String fullPath, String protocolVersion) {
        this.method = method;
        this.fullPath = fullPath;
        this.protocolVersion = protocolVersion;
    }

    public String getMethod() {
        return method;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getPath() {
        int index = fullPath.indexOf("?");
        if (index != -1) {
            return fullPath.substring(0, index);
        }
        return fullPath;
    }

    public static HttpRequestLine parse(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("Request Line은 비어있을 수 없습니다.");
        }
        String[] tokens = requestLine.trim().split("\\s+");
        if (tokens.length < 3) {
            throw new IllegalArgumentException("유효하지 않은 Request Line 형식입니다.");
        }
        return new HttpRequestLine(tokens[0], tokens[1], tokens[2]);
    }

    @Override
    public String toString() {
        return method + " " + fullPath + " " + protocolVersion;
    }
}
