package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {
    private static final String LINE_SEPARATOR = "\r\n";

    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpResponse(final HttpStatus status, final Map<String, String> headers, final byte[] body) {
        this.status = Objects.requireNonNull(status);
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = body.clone();
    }

    static HttpResponse of(final HttpStatus status, final byte[] body) {
        return new HttpResponse(status, Map.of(), body);
    }

    HttpResponse withHeader(final String name, final String value) {
        final var updatedHeaders = new LinkedHashMap<>(headers);
        updatedHeaders.put(Objects.requireNonNull(name), Objects.requireNonNull(value));

        return new HttpResponse(status, updatedHeaders, body);
    }

    byte[] headerBytes() {
        final var responseHeader = new StringBuilder("HTTP/1.1 ")
                .append(status.statusLine())
                .append(LINE_SEPARATOR);

        headers.forEach((name, value) -> responseHeader
                .append(name)
                .append(": ")
                .append(value)
                .append(LINE_SEPARATOR));
        responseHeader.append(LINE_SEPARATOR);

        return responseHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    byte[] body() {
        return body.clone();
    }
}
