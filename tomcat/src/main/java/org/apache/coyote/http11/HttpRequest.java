package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public record HttpRequest(
        RequestLine requestLine,
        Map<String, String> headers,
        byte[] body
) {
    private static final String EMPTY_LINE = "";

    public static HttpRequest of(final RequestLine requestLine, final Map<String, String> headers, final byte[] body) {
        return new HttpRequest(requestLine, headers, body);
    }

    public static HttpRequest from(final List<String> requestLines) {
        validateRequest(requestLines);

        final int separatorIndex = getSeparatorIndex(requestLines);
        final RequestLine requestLine =
                RequestLine.from(requestLines.getFirst());

        final List<String> headerLines =
                requestLines.subList(1, separatorIndex);

        final List<String> bodyLines =
                requestLines.subList(
                        separatorIndex + 1,
                        requestLines.size()
                );

        return new HttpRequest(
                requestLine,
                parseHeaders(headerLines),
                parseBody(bodyLines)
        );
    }

    private static void validateRequest(List<String> requestLines) {
        if (requestLines == null || requestLines.isEmpty()) {
            throw new IllegalArgumentException(
                    "HTTP request must not be empty"
            );
        }
    }

    private static int getSeparatorIndex(final List<String> requestLines) {
        int separatorIndex = requestLines.indexOf(EMPTY_LINE);

        if (separatorIndex < 0) {
            throw new IllegalArgumentException(
                    "HTTP request must contain an empty line"
            );
        }

        return separatorIndex;
    }

    private static Map<String, String> parseHeaders(
            final List<String> headerLines
    ) {
        final Map<String, String> headers =
                new HashMap<>();

        for (final String headerLine : headerLines) {
            final String[] headerParts =
                    headerLine.split(":", 2);

            validateHeaderPart(headerLine, headerParts);
            final String name = headerParts[0]
                    .trim()
                    .toLowerCase(Locale.ROOT);

            final String value = headerParts[1].trim();

            headers.put(name, value);
        }

        return Map.copyOf(headers);
    }

    private static void validateHeaderPart(String headerLine, String[] headerParts) {
        if (headerParts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid HTTP header: " + headerLine
            );
        }
    }

    private static byte[] parseBody(
            final List<String> bodyLines
    ) {
        if (bodyLines.isEmpty()) {
            return new byte[0];
        }

        final String body = String.join("\r\n", bodyLines);

        return body.getBytes(StandardCharsets.UTF_8);
    }
}
