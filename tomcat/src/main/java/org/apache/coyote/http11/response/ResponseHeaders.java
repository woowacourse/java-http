package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ResponseHeaders {

    private final Map<String, List<String>> values = new LinkedHashMap<>();

    void add(String name, String value) {
        values.computeIfAbsent(name, ignored -> new ArrayList<>()).add(value);
    }

    String serialize() {
        StringBuilder headers = new StringBuilder();
        values.forEach((name, headerValues) ->
                headerValues.forEach(value -> headers.append(name)
                        .append(": ")
                        .append(value)
                        .append(" \r\n"))
        );
        return headers.toString();
    }
}
