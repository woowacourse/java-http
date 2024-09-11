package org.apache.catalina.http.body;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.exception.CatalinaException;

public class HttpRequestBody {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    public HttpRequestBody() {
        this(new HashMap<>());
    }

    public HttpRequestBody(Map<String, String> body) {
        this.body = body;
    }

    public static HttpRequestBody parseUrlEncoded(String urlEncoded) {
        Map<String, String> body = Arrays.stream(urlEncoded.split("&"))
                .map(parameter -> parameter.split("="))
                .collect(Collectors.toMap(
                        parameter -> parameter[KEY_INDEX],
                        parameter -> parameter[VALUE_INDEX]
                ));
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
