package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final byte[] body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest read(final InputStream inputStream) throws IOException {
        final String firstLine = readLine(inputStream);
        if (firstLine == null || firstLine.isBlank()) {
            return null;
        }

        final RequestLine requestLine = new RequestLine(firstLine);
        final Map<String, String> headers = readHeaders(inputStream);
        final int contentLength = parseContentLength(headers.get("Content-Length"));

        final byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IllegalArgumentException("요청 본문의 길이가 부족합니다.");
        }

        return new HttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> readHeaders(final InputStream inputStream)
            throws IOException {
        final Map<String, String> headers =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        while (true) {
            final String line = readLine(inputStream);
            if (line == null) {
                throw new IllegalArgumentException("요청 헤더가 끝나지 않았습니다.");
            }
            if (line.isEmpty()) {
                return headers;
            }

            final int colon = line.indexOf(':');
            if (colon <= 0) {
                throw new IllegalArgumentException("잘못된 요청 헤더: " + line);
            }

            final String name = line.substring(0, colon).trim();
            final String value = line.substring(colon + 1).trim();
            headers.put(name, value);
        }
    }

    private static int parseContentLength(final String value) {
        if (value == null) {
            return 0;
        }

        try {
            final int length = Integer.parseInt(value);
            if (length < 0) {
                throw new IllegalArgumentException("Content-Length가 음수입니다.");
            }
            return length;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length: " + value, e);
        }
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int next;

        while ((next = inputStream.read()) != -1) {
            if (next == '\n') {
                final String line = buffer.toString(StandardCharsets.ISO_8859_1);
                return line.endsWith("\r")
                        ? line.substring(0, line.length() - 1)
                        : line;
            }
            buffer.write(next);
        }

        if (buffer.size() == 0) {
            return null;
        }
        throw new IllegalArgumentException("요청 줄이 끝나지 않았습니다.");
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getBody() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public Map<String, String> getFormParameters() {
        final Map<String, String> parameters = new HashMap<>();

        for (String pair : getBody().split("&")) {
            if (pair.isEmpty()) {
                continue;
            }

            final String[] entry = pair.split("=", 2);
            final String name = URLDecoder.decode(entry[0], StandardCharsets.UTF_8);
            final String value = entry.length == 2
                    ? URLDecoder.decode(entry[1], StandardCharsets.UTF_8)
                    : "";

            parameters.put(name, value);
        }

        return parameters;
    }
}
