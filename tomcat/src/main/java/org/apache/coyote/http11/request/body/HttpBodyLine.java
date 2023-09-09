package org.apache.coyote.http11.request.body;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpBodyLine {
    private final Map<String, String> body;

    private HttpBodyLine(final Map<String, String> body) {
        this.body = body;
    }

    public static HttpBodyLine from(final String httpBodyLine) {
        if (httpBodyLine.isEmpty()) {
            return new HttpBodyLine(Collections.emptyMap());
        }
        return new HttpBodyLine(Arrays.stream(httpBodyLine.split("&"))
                .map(header -> header.split("="))
                .collect(Collectors.toMap(key -> key[0], value -> value[1])));
    }

    public static HttpBodyLine empty() {
        return new HttpBodyLine(Collections.emptyMap());
    }

    public Map<String, String> getBody() {
        return body;
    }
}
