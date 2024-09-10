package org.apache.catalina.request;

public class RequestLine {
    private static final String SPACE = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private final HttpMethod httpMethod;
    private final String path;
    private final VersionOfProtocol versionOfProtocol;


    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(SPACE);
        if (parts.length < 3) {
            throw new IllegalArgumentException("요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.httpMethod = HttpMethod.of(parts[0]);
        this.path = parts[1];
        this.versionOfProtocol = new VersionOfProtocol(parts[2]);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getPathWithoutQuery() {
        return path.split(QUERY_PARAMETER_DELIMITER, 2)[0];
    }

    public HttpProtocol getHttpProtocol() {
        return versionOfProtocol.getHttpProtocol();
    }

    public String getHttpVersion() {
        return versionOfProtocol.getHttpVersion();
    }
}
