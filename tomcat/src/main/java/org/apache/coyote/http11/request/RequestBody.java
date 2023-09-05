package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> values;

    public RequestBody(Map<String, String> values) {
        this.values = new HashMap<>();
        this.values.putAll(values);
    }

    public static RequestBody from(String body) {
        return new RequestBody(Arrays.stream(body.split("&"))
                .map(keyValue -> keyValue.split("="))
                .collect(Collectors.toMap(
                        s -> s[0],
                        s -> s[1]
                )));
    }

    public boolean containValue(String key) {
        return values.containsKey(key);
    }

    public String get(String key) {
        return values.get(key);
    }
}
