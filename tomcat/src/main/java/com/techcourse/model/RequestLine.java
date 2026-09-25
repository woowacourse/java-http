package com.techcourse.model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestLine {
    private final String method;
    private final String path;
    private final Map<String, List<String>>  queryParameters;
    private final String protocolVersion;

    public static RequestLine parse(String firstLine) {
        String[] parts = firstLine.split(" ");

        if (parts.length != 3 ||
                parts[0].isEmpty() ||
                parts[1].isEmpty() ||
                parts[2].isEmpty()) {
            throw new IllegalArgumentException("Invalid request line: " + firstLine);
        }

        return new RequestLine(parts[0], extractPath(parts[1]), extractQueryParameters(parts[1]), parts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    private static String extractPath(String requestTarget) {
        int queryIndex = requestTarget.indexOf('?');

        if (queryIndex == -1) {
            return requestTarget;
        }

        return requestTarget.substring(0, queryIndex);
    }

    private static Map<String, List<String>> extractQueryParameters(String requestTarget) {
        Map<String, List<String>> queryParameters = new HashMap<>();
        int queryIndex = requestTarget.indexOf('?');

        // 쿼리가 없는 경우
        if (queryIndex == -1) {
            return queryParameters;
        }

        String queryLine = requestTarget.substring(queryIndex + 1);
        String[] parts = queryLine.split("&", -1);


        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            String[] keyValue = part.split("=", 2);

            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid request line: " + requestTarget);
            }

            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            queryParameters.computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(value);
        }

        return queryParameters;
    }
    private RequestLine(String method, String path, Map<String, List<String>> queryParameters, String protocolVersion) {
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.protocolVersion = protocolVersion;
    }
}
