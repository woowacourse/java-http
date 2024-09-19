package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpPayload {
    private static final String PAYLOAD_DELIMITER = "=";

    private final Map<String, String> value;

    public HttpPayload(Map<String, String> value) {
        this.value = Collections.unmodifiableMap(value);
    }

    public static HttpPayload from(List<String> clientData) {
        if (!clientData.getLast().contains("&")) {
            return null;
        }

        Map<String, String> result = new HashMap<>();

        for (String line : clientData.getLast().split("&")) {
            String[] split = line.split(PAYLOAD_DELIMITER);
            result.put(split[0], split[1]);
        }

        return new HttpPayload(result);
    }

    public String find(String key) {
        return value.get(key);
    }

    public Map<String, String> getValue() {
        return value;
    }
}
