package org.apache.coyote.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {
    private static final Map<String, String> EMPTY = Collections.emptyMap();

    private final Map<String, String> body;

    public RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(final String requestBodyLine) {
        if (!requestBodyLine.contains("=")) {
            return new RequestBody(EMPTY);
        }
        final Map<String, String> body = getKeyAndValue(requestBodyLine);
        return new RequestBody(body);
    }

    private static Map<String, String> getKeyAndValue(String line) {
        final Map<String, String> body = new LinkedHashMap<>();
        Arrays.stream(line.split("&"))
                .forEach(string -> body.put(string.split("=")[0], string.split("=")[1]));
        return body;
    }

    @Override
    public String toString() {
        return body.keySet()
                .stream()
                .map(key -> key + "=" + body.get(key))
                .collect(Collectors.joining("&"));
    }
}
