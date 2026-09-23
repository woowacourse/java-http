package org.apache.coyote.http11;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestParams {

    private final Map<String, String> params;

    public RequestParams(Map<String, String> params) {
        this.params = Collections.unmodifiableMap(params);
    }

    public static RequestParams of(String queryString) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String[] parameters = queryString.split("&");
        for (String param : parameters) {
            putParams(param, params);
        }
        return new RequestParams(params);
    }

    public static RequestParams empty() {
        return new RequestParams(Collections.<String, String>emptyMap());
    }

    private static void putParams(String param, Map<String, String> params) throws UnsupportedEncodingException {
        String[] keyValue = param.split("=");
        if (keyValue.length == 2) {
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            String decodedValue = URLDecoder.decode(value, "UTF-8");
            params.put(key, value);
        }
    }

    public String getParams(String key) {
        return params.getOrDefault(key, "");
    }
}
