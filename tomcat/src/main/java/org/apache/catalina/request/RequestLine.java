package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final String SPACE = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private final HttpMethod httpMethod;
    private final String path;
    private final VersionOfProtocol versionOfProtocol;
    private Map<String, String> queryParam = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(SPACE);
        if (parts.length < 3) {
            throw new IllegalArgumentException(requestLine + ": 요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.httpMethod = HttpMethod.of(parts[0]);
        this.path = parts[1];
        this.versionOfProtocol = new VersionOfProtocol(parts[2]);
    }

    public boolean checkQueryParamIsEmpty() {
        return queryParam.isEmpty();
    }

    public void setQueryParam(Map<String, String> queryParam) {
        this.queryParam = new HashMap<>(queryParam);
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

    public Map<String, String> getQueryParam() {
        return new HashMap<>(queryParam);
    }
}
