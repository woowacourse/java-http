package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String FORM_TYPE_DELIMITER = "&";
    private static final String FORM_VALUE_DELIMITER = "=";

    private final Map<String, String> values;

    public RequestBody() {
        this.values = new HashMap<>();
    }

    public RequestBody(Map<String, String> values) {
        this.values = values;
    }

    public static RequestBody of(String request) {
        if (request.isEmpty()) {
            return new RequestBody(Map.of());
        }
        Map<String, String> toMap = Arrays.stream(request.split(FORM_TYPE_DELIMITER))
                .map(it -> it.split(FORM_VALUE_DELIMITER))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        return new RequestBody(toMap);
    }

    public String value(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException();
        }
        return values.get(key);
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public String get(String key) {
        return values.get(key);
    }
}
