package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {

    private final Map<String, String> params = new HashMap<>();

    public RequestParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    public static RequestParams of(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] parameters = queryString.split("&");
        for (String param : parameters) {
            putParams(param, params);
        }
        return new RequestParams(params);
    }

    private static void putParams(String param, Map<String, String> params) {
        String[] keyValue = param.split("=");
        if (keyValue.length == 2) {
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            params.put(key, value);
        }
    }

    public String getParams(String key) {
        return params.getOrDefault(key, "");
    }
}
