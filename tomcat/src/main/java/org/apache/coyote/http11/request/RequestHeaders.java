package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestHeaders {

    private static final String CONTENT_LENGTH = "content-length";

    private final Map<String, List<String>> headers;

    public RequestHeaders(final HttpRequestInput input) {
        this.headers = resolveHeaders(input);
    }

    private Map<String, List<String>> resolveHeaders(final HttpRequestInput input) {
        final Map<String, List<String>> headers = new HashMap<>();

        try {
            String line = input.readLine();

            while (line != null && !line.isBlank()) {
                final String[] header = line.split(":", 2);

                if (header.length != 2) {
                    throw new IllegalArgumentException("허용되지 않는 header line: " + line);
                }

                final String name = header[0]
                        .trim()
                        .toLowerCase(Locale.ROOT);

                final String value = header[1].trim();

                headers.computeIfAbsent(
                        name,
                        key -> new ArrayList<>()
                ).add(value);

                line = input.readLine();
            }

            return headers;
        } catch (IOException e) {
            throw new UncheckedIOException("HTTP Header를 읽는 중 오류가 발생했습니다.", e);
        }
    }

    public String getHeader(final String name) {
        final List<String> values = headers.get(
                name.toLowerCase(Locale.ROOT)
        );

        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.getFirst();
    }

    public List<String> getHeaders(final String name) {
        final List<String> values = headers.get(
                name.toLowerCase(Locale.ROOT)
        );

        if (values == null) {
            return List.of();
        }

        return List.copyOf(values);
    }

    public int getContentLength() {
        final List<String> values = headers.get(CONTENT_LENGTH);

        if (values == null || values.isEmpty()) {
            return 0;
        }

        String contentLength = null;

        for (String headerValue : values) {
            for (String value : headerValue.split(",")) {
                final String trimmedValue = value.trim();

                if (contentLength == null) {
                    contentLength = trimmedValue;
                    continue;
                }

                if (!contentLength.equals(trimmedValue)) {
                    throw new IllegalArgumentException("conflicting Content-Length: " + values);
                }
            }
        }

        if (contentLength == null) {
            return 0;
        }

        return parseContentLength(contentLength);
    }

    private int parseContentLength(final String contentLength) {
        try {
            final int length = Integer.parseInt(contentLength);

            if (length < 0) {
                throw new IllegalArgumentException("Invalid Content-Length: " + contentLength);
            }

            return length;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Content-Length: " + contentLength, e);
        }
    }
}