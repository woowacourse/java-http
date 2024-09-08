package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeader {

    private final Map<String, List<String>> values;

    public HttpRequestHeader(Map<String, List<String>> values) {
        this.values = Map.copyOf(values);
    }

    public int getContentLength() {
        if (values.containsKey("Content-Length")) {
            return Integer.parseInt(values.get("Content-Length").getFirst());
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
