package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int SIZE = 2;
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_DELIMITER = "=";
    private static final String EMPTY_PAYLOAD = "";

    private final Map<String, String> payloads;

    public RequestBody(String payloads) {
        this.payloads = parseValues(payloads);
    }

    public static RequestBody empty() {
        return new RequestBody(EMPTY_PAYLOAD);
    }

    public Map<String, String> parseValues(String values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(values.split(PARAMETER_DELIMITER))
                .map(param -> param.trim().split(KEY_DELIMITER, SIZE))
                .collect(Collectors.toMap(
                        result -> result[KEY_INDEX],
                        result -> result[VALUE_INDEX])
                );
    }

    public Map<String, String> getPayloads() {
        return payloads;
    }
}
