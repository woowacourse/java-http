package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestLine {

    private static final int COMPONENT_COUNT = 3;

    private final Method method;
    private final String path;
    private final String protocolVersion;
    private final String queryString;
    private final Map<String, String> queryParameters;

    public RequestLine(final String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Request line은 비어 있을 수 없습니다.");
        }

        final String[] components = input.trim().split("\\s+");
        if (components.length != COMPONENT_COUNT) {
            throw new IllegalArgumentException("잘못된 Request line입니다: " + input);
        }

        this.method = Method.from(components[0]);
        this.path = extractPath(components[1]);
        this.queryString = extractQueryString(components[1]);
        this.queryParameters = Collections.unmodifiableMap(parseQueryParameters(queryString));
        this.protocolVersion = components[2];
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    private String extractPath(final String requestTarget) {
        final int queryStart = requestTarget.indexOf('?');
        if (queryStart < 0) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryStart);
    }

    private String extractQueryString(final String requestTarget) {
        final int queryStart = requestTarget.indexOf('?');
        if (queryStart < 0 || queryStart == requestTarget.length() - 1) {
            return "";
        }
        return requestTarget.substring(queryStart + 1);
    }

    private Map<String, String> parseQueryParameters(final String query) {
        if (query.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parameters = new LinkedHashMap<>();
        for (String parameter : query.split("&")) {
            final String[] keyValue = parameter.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }
            parameters.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return parameters;
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
