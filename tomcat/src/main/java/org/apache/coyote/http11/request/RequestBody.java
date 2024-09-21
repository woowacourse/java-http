package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private final Map<String, String> value;

    public RequestBody() {
        this.value = new HashMap<>();
    }

    public RequestBody(String rawValue) {
        if (rawValue.isEmpty()) {
            this.value = new HashMap<>();
            return;
        }
        this.value = Arrays.stream(rawValue.split("&"))
                .map(pair -> pair.split("=", 2))
                .collect(Collectors.toMap(splitPair -> splitPair[0], splitPair -> splitPair[1]));
    }

    public RequestBody(Map<String, String> value) {
        this.value = value;
    }

    public RequestBody(char[] rawBody) {
        this(new String(rawBody));
    }

    public String getValue(String key) {
        if (value.containsKey(key)) {
            return value.get(key);
        }
        throw new IllegalArgumentException("키가 없습니다.");
    }
}
