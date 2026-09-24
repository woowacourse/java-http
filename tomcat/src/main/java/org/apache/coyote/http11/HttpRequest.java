package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "content-length";

    private final RequestLine requestLine;
    private final Map<String, List<String>> headers;
    private final RequestBody body;

    public HttpRequest(final BufferedReader reader) {
        requestLine = resolveRequestLine(reader);
        headers = resolveHeaders(reader);
        body = resolveBody(reader);
    }

    private RequestLine resolveRequestLine(final BufferedReader reader) {
        try {
            final String line = reader.readLine();

            if (line == null || line.isBlank()) {
                throw new IllegalArgumentException("Request Line이 존재하지 않습니다.");
            }

            return new RequestLine(line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Map<String, List<String>> resolveHeaders(final BufferedReader reader) {
        final Map<String, List<String>> headers = new HashMap<>();

        try {
            String line = reader.readLine();

            while (line != null && !line.isBlank()) {
                final String[] header = line.split(":", 2);

                if (header.length != 2) {
                    throw new IllegalArgumentException("혀용되지 않는 header line : " + line);
                }

                final String name = header[0]
                        .trim()
                        .toLowerCase(Locale.ROOT);

                final String value = header[1].trim();

                headers.computeIfAbsent(name, key -> new ArrayList<>())
                        .add(value);

                line = reader.readLine();
            }

            return headers;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RequestBody resolveBody(final BufferedReader reader) {
        final int contentLength = resolveContentLength();

        if (contentLength == 0) {
            return new RequestBody("");
        }

        final char[] buffer = new char[contentLength];

        try {
            int offset = 0;

            while (offset < contentLength) {
                final int read = reader.read(
                        buffer,
                        offset,
                        contentLength - offset
                );

                if (read == -1) {
                    throw new IllegalArgumentException("Content-Length보다 Body가 짧습니다.");
                }

                offset += read;
            }

            return new RequestBody(new String(buffer));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int resolveContentLength() {
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

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
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

    public RequestBody getBody() {
        return body;
    }
}
