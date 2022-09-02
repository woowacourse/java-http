package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpParser;

public class RequestHeaders {

    private final Map<String, String> values;

    public RequestHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestHeaders from(final String lines) {
        return new RequestHeaders(HttpParser.parseHeaders(lines));
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String get(final String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
