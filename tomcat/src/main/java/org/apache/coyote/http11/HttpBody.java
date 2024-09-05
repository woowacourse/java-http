package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpBody {

    private final Map<String, String> body;

    public HttpBody(Map<String, String> body) {
        this.body = body;
    }

    public static HttpBody parseUrlEncoded(String urlEncoded) {
        Map<String, String> body = new HashMap<>();
        String[] keyAndValues = urlEncoded.split("&");
        for (String keyAndValue : keyAndValues) {
            String[] keyValue = keyAndValue.split("=");
            body.put(keyValue[0], keyValue[1]);
        }
        return new HttpBody(body);
    }

    public static HttpBody empty() {
        return new HttpBody(new HashMap<>());
    }

    public boolean isNotEmpty() {
        return !body.isEmpty();
    }

    public String get(String key) {
        String value = body.get(key);
        if (value == null) {
            throw new IllegalArgumentException("key " + key + " not found");
        }
        return value;
    }
}
