package org.apache.catalina.http.body;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.exception.CatalinaException;

public class HttpRequestBody {

    private final Map<String, String> body;

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public HttpRequestBody() {
        this(new HashMap<>());
    }

    public static HttpRequestBody parseUrlEncoded(String urlEncoded) {
        Map<String, String> body = new HashMap<>();
        String[] keyAndValues = urlEncoded.split("&");
        for (String keyAndValue : keyAndValues) {
            String[] keyValue = keyAndValue.split("=");
            body.put(keyValue[0], keyValue[1]);
        }
        return new HttpRequestBody(body);
    }

    public String get(String key) {
        String value = body.get(key);
        if (value == null) {
            throw new CatalinaException("Key " + key + " not found");
        }
        return value;
    }

    public void add(String key, String value) {
        body.put(key, value);
    }
}
