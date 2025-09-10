package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, List<String>> headers = new HashMap<>();

    public static ResponseHeaders empty() {
        return new ResponseHeaders();
    }

    public void addHeader(final String key, final String value) {
        this.headers.computeIfAbsent(key, k -> new ArrayList<>())
                .add(value);
    }

    public void addAll(final ResponseHeaders other) {
        other.headers.forEach((key, valueList) -> {
            valueList.forEach(value -> this.addHeader(key, value));
        });
    }

    public String toHeaderString() {
        final StringBuilder headerString = new StringBuilder();
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (final String value : entry.getValue()) {
                headerString.append(entry.getKey())
                        .append(": ")
                        .append(value)
                        .append("\r\n");
            }
        }
        return headerString.toString();
    }
}
