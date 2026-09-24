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

    private final RequestLine requestLine;
    private final Map<String, List<String>> headers;

    public HttpRequest(final BufferedReader reader) {
        requestLine = resolveRequestLine(reader);
        headers = resolveHeaders(reader);
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
}
