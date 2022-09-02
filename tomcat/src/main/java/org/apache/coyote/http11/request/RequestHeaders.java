package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpParser;

public class RequestHeaders {

    private final Map<String, String> value;

    public RequestHeaders(final Map<String, String> value) {
        this.value = value;
    }

    public static RequestHeaders from(final String lines) {
        return new RequestHeaders(HttpParser.parseHeaders(lines));
    }

    public Map<String, String> getValue() {
        return value;
    }

    public String get(final String key) {
        return value.get(key);
    }

    @Override
    public String toString() {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
