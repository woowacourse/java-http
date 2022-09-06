package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RequestHeaders {

    private final Map<String, String> values;

    private RequestHeaders(Map<String, String> values) {
        this.values = values;
    }

    public static RequestHeaders of(List<String> lines) {
        return new RequestHeaders(lines.stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(splitLine -> splitLine[0], splitLine -> splitLine[1])));
    }

    public int getContentLength() {
        return Integer.parseInt(values.getOrDefault("Content-Length", String.valueOf(0)));
    }

    public String getHeader(String headerName) {
        return values.get(headerName);
    }

    public String getOrCreateJSessionId() {
        return this.values.getOrDefault("JSESSIONID", String.valueOf(UUID.randomUUID()));
    }
}
