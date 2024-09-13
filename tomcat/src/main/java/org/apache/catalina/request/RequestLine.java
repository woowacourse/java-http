package org.apache.catalina.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.http.VersionOfProtocol;
import org.apache.catalina.parser.HttpRequestParser;

public class RequestLine {

    private static final String SPACE = " ";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    private static final int REQUEST_LINE_PARTS_COUNT = 3;
    public static final int PATH_WITHOUT_QUERY_INDEX = 0;
    public static final int PATH_QUERY_PARAMETER_INDEX = 1;
    private static final int PATH_LIMIT = 2;

    private final HttpMethod httpMethod;
    private final String path;
    private final VersionOfProtocol versionOfProtocol;
    private final Map<String, String> queryParam;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(SPACE);
        if (parts.length != REQUEST_LINE_PARTS_COUNT) {
            throw new IllegalArgumentException(requestLine + ": 요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.httpMethod = HttpMethod.of(parts[HTTP_METHOD_INDEX]);
        this.path = parts[PATH_INDEX];
        this.versionOfProtocol = new VersionOfProtocol(parts[PROTOCOL_VERSION_INDEX]);
        this.queryParam = findQueryParams(path);
    }

    private Map<String, String> findQueryParams(String path) {
        String[] separationUrl = path.split(QUERY_PARAMETER_DELIMITER, PATH_LIMIT);
        if (separationUrl.length >= PATH_LIMIT) {
            List<String> params = List.of(separationUrl[PATH_QUERY_PARAMETER_INDEX].split(QUERY_PARAMETER_SEPARATOR));
            return HttpRequestParser.parseParamValues(params);
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
        return path.split(QUERY_PARAMETER_DELIMITER, PATH_LIMIT)[PATH_WITHOUT_QUERY_INDEX];
    }

    public VersionOfProtocol getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public Map<String, String> getQueryParam() {
        return new HashMap<>(queryParam);
    }
}
