package org.apache.coyote.http11.model;

import static org.apache.coyote.http11.model.StringFormat.CRLF;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> headers;

    private Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers empty() {
        return new Headers(new LinkedHashMap<>());
    }

    public static Headers of(final List<String> headerLines) {
        Map<String, String> collect = headerLines.stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        return new Headers(collect);
    }

    public void add(final String key, final String value) {
        this.headers.put(key, value);
    }

    public boolean hasHeader(final Header header) {
        return this.headers.containsKey(header.getKey());
    }

    public String getValue(final String key) {
        return headers.get(key);
    }

    public String getString() {
        List<String> headerLines = headers.entrySet()
                .stream()
                .map(keyValue -> String.join(": ",
                        keyValue.getKey(),
                        keyValue.getValue() + " ")
                ).collect(Collectors.toList());

        return String.join(CRLF, headerLines);
    }
}
