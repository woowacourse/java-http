package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, List<String>> values;

    public HttpRequestHeader(Map<String, List<String>> values) {
        this.values = Map.copyOf(values);
    }

    public int getContentLength() {
        if (values.containsKey(CONTENT_LENGTH)) {
            return Integer.parseInt(values.get(CONTENT_LENGTH).getFirst());
        }
        return 0;
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(";", entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
