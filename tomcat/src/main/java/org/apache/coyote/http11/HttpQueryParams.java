package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpQueryParams {

    private final Map<String, String> params;

    private HttpQueryParams(Map<String, String> params) {
        this.params = params;
    }

    public static HttpQueryParams fromPath(String fullPath) {
        Map<String, String> queryParams = new HashMap<>();
        int index = fullPath.indexOf("?");
        if (index != -1) {
            String queryString = fullPath.substring(index + 1);
            for (String pairs : queryString.split("&")) {
                String[] pair = pairs.split("=", 2);
                if (pair.length == 2) {
                    queryParams.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }
        return new HttpQueryParams(queryParams);
    }

    public static HttpQueryParams fromBody(String body) {
        Map<String, String> params = new HashMap<>();
        if (body != null && !body.isEmpty()) {
            for (String pairs : body.split("&")) {
                String[] keyValue = pairs.split("=", 2);
                if (keyValue.length == 2) {
                    params.put(
                            URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                    );
                }
            }
        }
        return new HttpQueryParams(params);
    }

    public String get(String key) {
        return params.get(key);
    }
}
