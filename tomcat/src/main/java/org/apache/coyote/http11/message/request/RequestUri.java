package org.apache.coyote.http11.message.request;

import java.util.HashMap;
import java.util.Map;

public class RequestUri {
    private final String path;
    private final Map<String, String> queryParams;

    private RequestUri(String path, Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }

    public static RequestUri from(String raw) {
        String path = extractPath(raw);
        Map<String, String> queryParams = extractQueryParams(raw);
        return new RequestUri(path, queryParams);
    }

    private static String extractPath(String raw) {
        int index = raw.indexOf("?");
        if (index < 0) {
            return raw;
        }

        return raw.substring(0, index);
    }

    private static Map<String, String> extractQueryParams(String raw) {
        int index = raw.indexOf("?");
        if (index < 0) {
            return new HashMap<>();
        }

        String queryString = raw.substring(index + 1);
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public String getPath() {
        return path;
    }
}
