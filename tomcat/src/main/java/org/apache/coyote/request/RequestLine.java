package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final String PATH_PREFIX = "/";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int PARAMETER_COUNT = 3;

    private static final String QUERY_PARAMETER_PREFIX = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_NAME_VALUE_DELIMITER = "=";

    private static final int SPLIT_LIMIT = -1;

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParameters;

    public RequestLine(String rawRequestLine) {
        validateParameterCount(rawRequestLine);
        String[] parameters = rawRequestLine.split(DELIMITER);

        validateUri(parameters[URI_INDEX]);
        validateHttpVersion(parameters[HTTP_VERSION_INDEX]);

        this.method = HttpMethod.from(parameters[METHOD_INDEX]);
        this.path = parsePath(parameters[URI_INDEX]);
        this.queryParameters = parseQueryParameters(parameters[URI_INDEX]);
    }

    private void validateHttpVersion(String httpVersion) {
        if (!httpVersion.equals(HTTP_VERSION)) {
            throw new UncheckedServletException("HTTP 버전은 %s 만 허용됩니다.".formatted(HTTP_VERSION));
        }
    }

    private void validateUri(String uri) {
        if (!uri.startsWith(PATH_PREFIX)) {
            throw new UncheckedServletException("URI는 %s 로 시작해야 합니다.".formatted(PATH_PREFIX));
        }
    }

    private void validateParameterCount(String rawRequestLine) {
        if (rawRequestLine.split(DELIMITER).length != PARAMETER_COUNT) {
            throw new UncheckedServletException(String.format("Request line의 인자는 %d개여야 합니다.", PARAMETER_COUNT));
        }
    }

    private String parsePath(String uri) {
        if (uri.contains(QUERY_PARAMETER_PREFIX)) {
            return uri.substring(0, uri.indexOf(QUERY_PARAMETER_PREFIX));
        }

        return uri;
    }

    private Map<String, String> parseQueryParameters(String uri) {
        if (!uri.contains(QUERY_PARAMETER_PREFIX)) {
            return Collections.emptyMap();
        }

        String queryString = uri.substring(uri.indexOf(QUERY_PARAMETER_PREFIX) + QUERY_PARAMETER_PREFIX.length());

        return Arrays.stream(queryString.split(QUERY_PARAMETER_DELIMITER, SPLIT_LIMIT))
                .collect(Collectors.toMap(this::parseKey, this::parseValue));
    }

    public String parseKey(String queryString) {
        validateQueryFormat(queryString);
        return queryString.substring(0, queryString.indexOf(QUERY_NAME_VALUE_DELIMITER));
    }

    public String parseValue(String queryString) {
        validateQueryFormat(queryString);
        return queryString.substring(
                queryString.indexOf(QUERY_NAME_VALUE_DELIMITER) + QUERY_NAME_VALUE_DELIMITER.length());
    }

    private void validateQueryFormat(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            throw new UncheckedServletException("형식이 올바르지 않은 쿼리가 포함되어 있습니다.");
        }
        if (!queryString.contains(QUERY_NAME_VALUE_DELIMITER)) {
            throw new UncheckedServletException("형식이 올바르지 않은 쿼리가 포함되어 있습니다.");
        }
        if (queryString.startsWith(QUERY_NAME_VALUE_DELIMITER) || queryString.endsWith(QUERY_NAME_VALUE_DELIMITER)) {
            throw new UncheckedServletException("형식이 올바르지 않은 쿼리가 포함되어 있습니다.");
        }
    }

    public boolean hasMethod(HttpMethod httpMethod) {
        return method == httpMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
