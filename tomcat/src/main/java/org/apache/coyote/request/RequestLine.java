package org.apache.coyote.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private static final int PARAMETER_COUNT = 3;

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParameters;

    public RequestLine(String requestLine) {
        validateParameterCount(requestLine);
        String[] parameters = requestLine.split(" ");

        validateUri(parameters[1]);
        validateHttpVersion(parameters[2]);

        this.method = HttpMethod.from(parameters[0]);
        this.path = parseEndpoint(parameters[1]);
        this.queryParameters = parseQueryParams(parameters[1]);
    }

    private void validateHttpVersion(String httpVersion) {
        if (!httpVersion.equals("HTTP/1.1")) {
            throw new UncheckedServletException("HTTP 버전은 HTTP/1.1 만 허용됩니다.");
        }
    }

    private void validateUri(String uri) {
        if (!uri.startsWith("/")) {
            throw new UncheckedServletException("URI는 / 로 시작해야 합니다.");
        }
    }

    private void validateParameterCount(String rawRequestLine) {
        if (rawRequestLine.split(" ").length != PARAMETER_COUNT) {
            throw new UncheckedServletException(String.format("Request line의 인자는 %d개여야 합니다.", PARAMETER_COUNT));
        }
    }

    private String parseEndpoint(String uri) {
        return uri.split("\\?")[0];
    }

    private Map<String, String> parseQueryParams(String uri) {
        if (!uri.contains("?")) {
            return Collections.emptyMap();
        }

        String queryString = uri.split("\\?")[1];

        return Arrays.stream(queryString.split("&"))
                .map(rawQuery -> rawQuery.split("="))
                .filter(query -> query.length == 2)
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
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
