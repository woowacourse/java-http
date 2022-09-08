package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {

    private final Map<String, String> value;

    public HttpResponseHeader(final Map<String, String> value) {
        this.value = value;
    }

    public void add(String key, String value) {
        this.value.put(key, value);
    }

    public String getAll() {
        return value.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
