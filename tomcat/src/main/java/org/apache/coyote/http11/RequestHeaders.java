package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> value;

    private RequestHeaders(final Map<String, String> value) {
        this.value = value;
    }

    public static RequestHeaders of(final List<String> inputs) {
        Map<String, String> headers = new HashMap<>();
        for (String header : inputs.subList(1, inputs.size())) {
            String[] splitHeader = header.split(": ", 2);
            headers.put(splitHeader[0], splitHeader[1]);
        }
        return new RequestHeaders(headers);
    }

    public String get(String key) {
        return value.get(key);
    }
}
