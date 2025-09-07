package org.apache.coyote.util;

import org.apache.coyote.dto.RequestInfo;

import java.util.HashMap;
import java.util.Map;

public class RequestLineParser {
    public static RequestInfo parse(final String requestLine) {
        final String[] parts = requestLine.split(" ");
        final String method = parts.length >= 1 ? parts[0].toUpperCase() : "GET";
        final String fullPath = parts.length >= 2 ? parts[1] : "/";

        String path = fullPath;
        Map<String,String> queryParams = new HashMap<>();

        if (fullPath.contains("?")) {
            String[] pathParts = fullPath.split("\\?", 2);
            path = pathParts[0];
            queryParams = parseQueryString(pathParts[1]);
        }
        return new RequestInfo(method, path, queryParams);
    }

    private static Map<String, String> parseQueryString(final String queryString) {
        Map<String, String> params = new HashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue[1];
            params.put(key, value);
        }
        return params;
    }


}
