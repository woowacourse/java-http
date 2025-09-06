package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record Http11Request(
        String method,
        String path,
        //Todo: header 일급객체 분리 [2025-09-05 17:22:13]
        LinkedHashMap<String, String> headers,
        byte[] body
) {

    public String parseResourcePath() {
        final int queryIndex = path.indexOf("?");
        String resourcePath = path;
        if (queryIndex != -1) {
            resourcePath = path.substring(0, queryIndex);
        }
        return resourcePath;
    }

    public Map<String, String> parseQuery() {
        final int queryIndex = path.indexOf("?");
        if (queryIndex == -1) {
            return new HashMap<>();
        }

        final String pathQuery = path.substring(queryIndex + 1);
        final String[] splitPathQuery = pathQuery.split("&");
        final Map<String, String> queries = new HashMap<>();
        for (String query : splitPathQuery) {
            String[] keyValue = query.split("=");
            if (keyValue.length != 2) {
                return null;
            }
            queries.put(keyValue[0], keyValue[1]);
        }
        return queries;
    }
}
