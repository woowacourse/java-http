package org.apache.coyote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private final Map<String, String> values;

    private RequestHeader(final Map<String, String> values) {
        this.values = values;
    }

    public static RequestHeader parse(final String lines) {
        return new RequestHeader(toMap(List.of(lines.split("\r\n"))));
    }

    private static Map<String, String> toMap(List<String> lines) {
        Map<String, String> values = new HashMap<>();

        for (String line : lines) {
            final List<String> value = List.of(line.split(": "));
            values.put(value.get(0), value.get(1));
        }

        return values;
    }

    public String get(final String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "values=" + values +
                '}';
    }
}
