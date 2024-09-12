package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.http.VersionOfProtocol;
import org.apache.catalina.parser.RequestParser;

public class RequestLine {
    private static final String SPACE = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";

    private final HttpMethod httpMethod;
    private final String path;
    private final VersionOfProtocol versionOfProtocol;
    private Map<String, String> queryParam;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(SPACE);
        if (parts.length < 3) {
            throw new IllegalArgumentException(requestLine + ": 요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.httpMethod = HttpMethod.of(parts[0]);
        this.path = parts[1];
        this.versionOfProtocol = new VersionOfProtocol(parts[2]);

        setQueryParams(path);
    }

    private void setQueryParams(String path) {
        String[] separationUrl = path.split(QUERY_PARAMETER_DELIMITER, 2);
        queryParam = new HashMap<>();
        if (separationUrl.length >= 2) {
            queryParam = RequestParser.parseParamValues(separationUrl[1]);
        }
    }

    public boolean checkQueryParamIsEmpty() {
        return queryParam.isEmpty();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public boolean isSameHttpMethod(HttpMethod httpMethod) {
        return httpMethod == this.httpMethod;
    }

    public String getPathWithoutQuery() {
        return path.split(QUERY_PARAMETER_DELIMITER, 2)[0];
    }

    public String getPath() {
        return path;
    }

    public VersionOfProtocol getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public Map<String, String> getQueryParam() {
        return new HashMap<>(queryParam);
    }
}
