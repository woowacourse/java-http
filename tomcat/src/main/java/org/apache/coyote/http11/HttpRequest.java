package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest readFrom(InputStream inputStream) throws IOException {
        var input = new BufferedInputStream(inputStream);
        String line = readHttpLine(input);
        if (line == null) {
            return null;
        }

        RequestLine requestLine = RequestLine.parse(line);
        Map<String, String> headers = readHeaders(input);
        String body = readBody(input, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    public String method() {
        return requestLine.method();
    }

    public String path() {
        return requestLine.path();
    }

    public String httpVersion() {
        return requestLine.httpVersion();
    }

    public String header(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String body() {
        return body;
    }

    private static String readHttpLine(InputStream input) throws IOException {
        final var bytes = new ByteArrayOutputStream();
        int value;

        while ((value = input.read()) != -1) {
            if (value == '\n') {
                final String line = bytes.toString(StandardCharsets.UTF_8);
                return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
            }
            bytes.write(value);
        }

        return bytes.size() == 0 ? null : bytes.toString(StandardCharsets.UTF_8);
    }

    private static Map<String, String> readHeaders(InputStream input) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = readHttpLine(input)) != null) {
            if (line.isEmpty()) {
                return headers;
            }

            final String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new IOException("잘못된 요청 헤더입니다.");
            }
            headers.put(parts[0].trim().toLowerCase(Locale.ROOT), parts[1].trim());
        }

        throw new IOException("요청 헤더가 완전히 도착하지 않았습니다.");
    }

    private static String readBody(InputStream input, Map<String, String> headers) throws IOException {
        final int contentLength;
        try {
            contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        } catch (NumberFormatException e) {
            throw new IOException("잘못된 Content-Length입니다.");
        }

        if (contentLength < 0) {
            throw new IOException("잘못된 Content-Length입니다.");
        }

        final byte[] body = input.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IOException("요청 본문이 완전히 도착하지 않았습니다.");
        }

        return new String(body, StandardCharsets.UTF_8);
    }
}
