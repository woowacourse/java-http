package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
    public HttpRequest(final String method, final String path, final String version, final Map<String, String> headers) {
        this(new RequestLine(method, RequestUri.from(path), version), headers, "");
    }

    public HttpRequest {
        headers = Map.copyOf(headers);
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var requestLine = RequestLine.from(reader.readLine());

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

        final var request = new HttpRequest(
                requestLine,
                parsedHeaders,
                ""
        );
        return request.withBody(readBody(reader, request));
    }

    private static String readBody(final BufferedReader reader, final HttpRequest request) throws IOException {
        if (!request.method().equals("POST")) {
            return "";
        }

        final var contentLength = Integer.parseInt(request.header("Content-Length"));
        final var buffer = new char[contentLength];
        var totalRead = 0;

        while (totalRead < contentLength) {
            final var read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("Request body ended before Content-Length");
            }
            totalRead += read;
        }

        return new String(buffer);
    }

    public String header(final String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String method() {
        return requestLine.method();
    }

    public String path() {
        return requestLine.uri().path();
    }

    public String version() {
        return requestLine.version();
    }

    public String queryParameter(final String name) {
        return requestLine.uri().queryParameter(name);
    }

    public HttpRequest withBody(final String requestBody) {
        return new HttpRequest(requestLine, headers, requestBody);
    }
}
