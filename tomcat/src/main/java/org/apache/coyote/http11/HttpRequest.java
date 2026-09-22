package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
    public HttpRequest(final String method, final String path, final String version, final Map<String, String> headers) {
        this(method, path, version, headers, "");
    }

    public HttpRequest {
        headers = Map.copyOf(headers);
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("Request line is required");
        }

        final var requestParts = requestLine.trim().split("\\s+", 3);

        if (requestParts.length != 3) {
            throw new IOException("Invalid request line");
        }

        final var parsedHeaders = new HashMap<String, String>();
        String headerLine;

        while ((headerLine = reader.readLine()) != null && !headerLine.isBlank()) {
            final var colonIndex = headerLine.indexOf(':');

            if (colonIndex <= 0) {
                throw new IOException("Invalid request header");
            }

            final var name = headerLine.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            final var value = headerLine.substring(colonIndex + 1).trim();

            parsedHeaders.put(name, value);
        }

        return new HttpRequest(
                requestParts[0],
                requestParts[1],
                requestParts[2],
                parsedHeaders,
                ""
        );
    }

    public String header(final String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public HttpRequest withBody(final String requestBody) {
        return new HttpRequest(method, path, version, headers, requestBody);
    }
}
