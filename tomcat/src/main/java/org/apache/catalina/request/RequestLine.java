package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.http.VersionOfProtocol;
import org.apache.catalina.parser.HttpRequestParser;

public class RequestLine {

    private static final String SPACE = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";

    private final HttpMethod httpMethod;
    private final String path;
    private final VersionOfProtocol versionOfProtocol;
    private final Map<String, String> queryParam;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(SPACE);
        if (parts.length < 3) {
            throw new IllegalArgumentException(requestLine + ": 요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.httpMethod = HttpMethod.of(parts[0]);
        this.path = parts[1];
        this.versionOfProtocol = new VersionOfProtocol(parts[2]);
        this.queryParam = findQueryParams(path);
    }

    private Map<String, String> findQueryParams(String path) {
        String[] separationUrl = path.split(QUERY_PARAMETER_DELIMITER, 2);
        if (separationUrl.length >= 2) {
            return HttpRequestParser.parseParamValues(separationUrl[1]);
        }
        return new HashMap<>();
    }

    public boolean checkQueryParamIsEmpty() {
        return queryParam.isEmpty();
    }

    public boolean isSameHttpMethod(HttpMethod httpMethod) {
        return httpMethod == this.httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getPathWithoutQuery() {
        return path.split(QUERY_PARAMETER_DELIMITER, 2)[0];
    }

    public VersionOfProtocol getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public Map<String, String> getQueryParam() {
        return new HashMap<>(queryParam);
    }
}
