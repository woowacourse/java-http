package org.apache.catalina.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {
    private static final String SPACE = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
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
            queryParam = getParamValues(separationUrl[1]);
        }
    }

    private static Map<String, String> getParamValues(String params) {
        return Arrays.stream(params.split(QUERY_PARAMETER_SEPARATOR))
                .map(param -> param.split(QUERY_KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }

    public boolean checkQueryParamIsEmpty() {
        return queryParam.isEmpty();
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
