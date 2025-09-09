package org.apache.coyote.http11.httpresponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, List<String>> headers;

    public ResponseHeaders(final Map<String, List<String>> headers) {
        this.headers = new HashMap<>(headers);
    }

    public void add(final String key, final String value) {
        headers.computeIfAbsent(key, k -> new ArrayList<>())
                .add(value);
    }

    public String toResponseText() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                sb.append(entry.getKey())
                        .append(": ")
                        .append(value)
                        .append(" \r\n");
            }
        }

        return sb.toString();
    }
}
