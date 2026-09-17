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
            String[] kv = param.split("=");
            params.put(kv[0], kv[1]);
        }
        return new RequestParams(params);
    }

    public String getParams(String key) {
        return params.getOrDefault(key, "");
    }
}
