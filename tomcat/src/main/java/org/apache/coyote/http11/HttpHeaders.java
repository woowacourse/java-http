package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {

    private final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final List<String> headerNames = new ArrayList<>();

    public HttpHeaders() {
    }

    public HttpHeaders(final List<String> headerLines) throws IOException {
        for (String line : headerLines) {
            addHeader(line);
        }
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public void putHeader(final String name, final String value) {
        if (!headers.containsKey(name)) {
            headerNames.add(name);
        }
        headers.put(name, value);
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        for (String name : headerNames) {
            final String line = name + ": " + headers.get(name) + "\r\n";
            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void addHeader(final String line) throws IOException {
        final int separator = line.indexOf(':');
        if (separator <= 0) {
            throw new IOException("잘못된 요청 헤더입니다: " + line);
        }

        final String name = line.substring(0, separator).trim();
        if (name.isEmpty()) {
            throw new IOException("잘못된 요청 헤더입니다: " + line);
        }
        final String value = line.substring(separator + 1).trim();
        putHeader(name, value);
    }
}
