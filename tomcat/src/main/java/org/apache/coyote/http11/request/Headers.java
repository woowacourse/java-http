package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> headers;

    private Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers from(final List<String> request) {
        Map<String, String> headers = request.stream()
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(
                        splitLine -> splitLine[0],
                        splitLine -> splitLine[1]
                ));

        return new Headers(headers);
    }

    public String getHeader(final String header) {
        return headers.get(header);
    }
}
