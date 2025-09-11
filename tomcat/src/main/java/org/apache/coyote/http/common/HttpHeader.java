package org.apache.coyote.http.common;

import static org.apache.coyote.http.common.HttpConstants.CRLF;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHeader {

    private final Map<String, String> headers;

    public static HttpHeader from(final String rawHeader) {
        if (rawHeader == null || rawHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP 헤더는 null이거나 비어있을 수 없습니다");
        }

        final String[] headerLines = rawHeader.split(CRLF);

        final Map<String, String> headers = parseHeaders(headerLines);

        return new HttpHeader(headers);
    }

    public static HttpHeader from(final Map<String, String> headers) {
        return new HttpHeader(new HashMap<>(headers));
    }

    public static HttpHeader empty() {
        return new HttpHeader(new HashMap<>());
    }

    private static Map<String, String> parseHeaders(final String[] rawHeaders) {
        final Map<String, String> headers = new HashMap<>();

        for (final String rawHeader : rawHeaders) {
            final String line = rawHeader.trim();
            if (line.isEmpty()) {
                break;
            }
            parseHeaderLine(line, headers);
        }

        return headers;
    }

    private static void parseHeaderLine(final String rawHeader, final Map<String, String> headers) {
        final int colonIndex = rawHeader.indexOf(HttpConstants.HEADER_VALUE_SEPARATOR);
        if (colonIndex <= 0) {
            return;
        }
        final String headerName = rawHeader.substring(0, colonIndex).trim().toLowerCase();
        final String headerValue = rawHeader.substring(colonIndex + 1).trim();
        headers.put(headerName, headerValue);
    }

    public String get(final String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    public int getContentLength() {
        final String contentLengthValue = headers.get(HttpConstants.CONTENT_LENGTH_HEADER_NAME);
        if (contentLengthValue == null) {
            return 0;
        }

        try {
            return Integer.parseInt(contentLengthValue.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    public ContentType getContentType() {
        return ContentType.fromHeader(get(ContentType.HEADER_NAME));
    }

    public void add(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        headers.forEach((name, value) ->
                sb.append(capitalizeFirstLetter(name))
                        .append(HttpConstants.HEADER_VALUE_SEPARATOR).append(" ")
                        .append(value).append(CRLF));

        return sb.toString();
    }

    private String capitalizeFirstLetter(final String str) {
        if (str.isEmpty()) {
            return str;
        }

        final String[] parts = str.split("-");
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append("-");
            }
            if (!parts[i].isEmpty()) {
                result.append(parts[i].substring(0, 1).toUpperCase())
                        .append(parts[i].substring(1).toLowerCase());
            }
        }

        return result.toString();
    }
}
