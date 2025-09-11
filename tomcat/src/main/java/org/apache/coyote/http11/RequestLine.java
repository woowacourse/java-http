package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, List<String>> parseUrlEncodedParams(final String data) {
        if (data == null || data.isBlank()) {
            return Collections.emptyMap();
        }
        final Map<String, List<String>> params = new HashMap<>();
        final String[] pairs = data.split("&");
        for (final String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }
        return params;
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
