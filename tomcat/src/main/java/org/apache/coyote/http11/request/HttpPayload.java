package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpPayload {
    private static final String PAYLOAD_KEY_VALUE_DELIMITER = "=";
    public static final String PAYLOAD_DELIMITER = "&";

    private final Map<String, String> value;

    public HttpPayload(Map<String, String> value) {
        this.value = Collections.unmodifiableMap(value);
    }

    public static HttpPayload from(List<String> data) {
        if (validateLastLineIsPayload(data)) {
            return null;
        }

        Map<String, String> buffer = new HashMap<>();

        for (String currentLine : data.getLast().split(PAYLOAD_DELIMITER)) {
            String[] keyValue = currentLine.split(PAYLOAD_KEY_VALUE_DELIMITER);
            String payloadKey =keyValue[0];
            String payloadValue = keyValue[1];
            buffer.put(payloadKey, payloadValue);
        }

        return new HttpPayload(buffer);
    }

    private static boolean validateLastLineIsPayload(List<String> clientData) {
        return !clientData.getLast().contains(PAYLOAD_DELIMITER);
    }

    public String find(String key) {
        return value.get(key);
    }

    public Map<String, String> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "HttpPayload{" +
               "value=" + value +
               '}';
    }
}
