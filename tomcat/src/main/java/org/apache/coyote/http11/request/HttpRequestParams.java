package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParams {

    private final Map<String, String> params;

    public HttpRequestParams(Map<String, String> params) {
        this.params = Collections.unmodifiableMap(params);
    }

    public static HttpRequestParams of(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] parameters = queryString.split("&");
        for (String param : parameters) {
            putParams(param, params);
        }
        return new HttpRequestParams(params);
    }

    private static void putParams(String param, Map<String, String> params) {
        String[] keyValue = param.split("=");
        if (keyValue.length == 2) {
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
            params.put(key, decodedValue);
        }
    }

    public String getParams(String key) {
        return params.getOrDefault(key, "");
    }

    public String toString() {
        return params.entrySet().toString();
    }
}
