package org.apache.common;

import java.util.HashMap;
import java.util.Map;

public class HttpBody {

    private final Map<String, String> params;

    private HttpBody(Map<String, String> params) {
        this.params = params;
    }

    public static HttpBody of(String content) {
        if (content.isBlank()) {
            return new HttpBody(new HashMap<>());
        }

        Map<String, String> params = parseParams(content);
        return new HttpBody(params);
    }

    private static Map<String, String> parseParams(String content) {
        Map<String, String> params = new HashMap<>();
        String[] queries = content.split("&");
        for (String query : queries) {
            String[] keyValue = query.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
