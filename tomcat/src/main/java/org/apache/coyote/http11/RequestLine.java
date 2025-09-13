package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final Map<String, List<String>> queryParams;
    private final HttpVersion version;

    private RequestLine(
            final HttpMethod method,
            final String path,
            final Map<String, List<String>> queryParams,
            final HttpVersion version
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.version = version;
    }

    public static RequestLine from(final String line) {
        if (line == null || line.isBlank()) {
            return createInvalid();
        }
        final int firstSpace = line.indexOf(' ');
        final int lastSpace = line.lastIndexOf(' ');
        final var method = HttpMethod.valueOf(line.substring(0, firstSpace));
        final var version = HttpVersion.from(line.substring(lastSpace + 1));
        final String requestTarget = line.substring(firstSpace + 1, lastSpace);
        String path = requestTarget;
        String queryString = "";
        if (requestTarget.contains("?")) {
            final int queryIndex = requestTarget.indexOf("?");
            path = requestTarget.substring(0, queryIndex);
            queryString = requestTarget.substring(queryIndex + 1);
        }
        final var queryParams = parseUrlEncodedParams(queryString);
        return new RequestLine(method, path, queryParams, version);
    }

    public static RequestLine createInvalid() {
        return new RequestLine(HttpMethod.UNKNOWN, "/", Map.of(), HttpVersion.HTTP_1_1);
    }

    public static Map<String, List<String>> parseUrlEncodedParams(final String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return Collections.emptyMap();
        }
        final Map<String, List<String>> queryParameters = new HashMap<>();
        final String[] keyValuePairs = queryString.split("&");
        for (final String keyValuePair : keyValuePairs) {
            parseSingleParameter(keyValuePair)
                    .ifPresent(parameter -> queryParameters.computeIfAbsent(parameter.key(), k -> new ArrayList<>())
                            .add(parameter.value()));
        }
        return queryParameters;
    }

    private static Optional<QueryParameter> parseSingleParameter(final String keyValuePair) {
        if (keyValuePair == null || keyValuePair.isBlank() || keyValuePair.startsWith("=")) {
            return Optional.empty();
        }
        try {
            final int equalsIndex = keyValuePair.indexOf("=");
            if (equalsIndex == -1) {
                final String decodedKey = URLDecoder.decode(keyValuePair, StandardCharsets.UTF_8);
                return Optional.of(new QueryParameter(decodedKey, ""));
            }
            final String keyPart = keyValuePair.substring(0, equalsIndex);
            final String valuePart = keyValuePair.substring(equalsIndex + 1);
            final String decodedKey = URLDecoder.decode(keyPart, StandardCharsets.UTF_8);
            final String decodedValue = URLDecoder.decode(valuePart, StandardCharsets.UTF_8);
            return Optional.of(new QueryParameter(decodedKey, decodedValue));
        } catch (final IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
