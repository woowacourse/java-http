package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Headers {

    private static final String FIELD_VALUE_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";
    private static final String SPACE = " ";
    private static final List<String> TEXT_TYPES = List.of("text/", "application/json", "application/xml", "application/x-www-form-urlencoded");
    private static final int FIELD_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static final String REQUEST_HEADER_COOKIE = "Cookie";

    private final Map<String, String> headers;

    public Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static Headers form(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new ConcurrentHashMap<>();

        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null) {
            if (headerLine.isBlank()) {
                break;
            }
            final String[] header = headerLine.split(FIELD_VALUE_SEPARATOR);
            headers.put(header[FIELD_INDEX], header[VALUE_INDEX]);
        }
        return new Headers(headers);
    }

    public void add(final String field, final String value) {
        headers.put(field, value);
    }

    public boolean containsField(final String field) {
        return headers.containsKey(field);
    }

    public static boolean isTextType(final String contentType) {
        return TEXT_TYPES.stream().anyMatch(contentType::startsWith);
    }

    public String getByField(final String field) {
        return headers.getOrDefault(field, null);
    }

    public HttpCookies getCookie() {
        return new HttpCookies(headers.get(REQUEST_HEADER_COOKIE));
    }

    public String format() {
        final List<String> headerLines = new ArrayList<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headerLines.add(String.join(FIELD_VALUE_SEPARATOR, entry.getKey(), entry.getValue()) + SPACE);
        }
        return String.join(CRLF, headerLines);
    }
}
