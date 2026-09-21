package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record HttpRequest(
        HttpRequestLine requestLine,
        Map<String, String> headers,
        HttpCookie cookie,
        Map<String, String> body
) {
    private static final String COLON = ":";
    private static final int HEADER_PARTS_COUNT = 2;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private static final int LF = '\n';
    private static final int END_OF_STREAM = -1;

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        final HttpRequestLine requestLine = HttpRequestLine.from(readLine(bufferedInputStream));
        final Map<String, String> headers = readHeaders(bufferedInputStream);

        return new HttpRequest(
                requestLine,
                headers,
                readCookie(headers),
                readBody(bufferedInputStream, headers)
        );
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();

        int read = inputStream.read();
        while (read != END_OF_STREAM && read != LF) {
            line.write(read);
            read = inputStream.read();
        }

        if (read == END_OF_STREAM && line.size() == 0) {
            return null;
        }

        return line.toString(HttpRequestLine.CHARSET).stripTrailing();
    }

    private static Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = readLine(inputStream);

        while (line != null && !line.isEmpty()) {
            final String[] header = line.split(COLON, HEADER_PARTS_COUNT);

            if (header.length == HEADER_PARTS_COUNT) {
                headers.put(header[HEADER_KEY_INDEX].trim(), header[HEADER_VALUE_INDEX].trim());
            }

            line = readLine(inputStream);
        }

        if (line == null) {
            throw new IllegalArgumentException("헤더가 빈 줄로 끝나지 않았습니다.");
        }

        return Collections.unmodifiableMap(headers);
    }

    private static HttpCookie readCookie(final Map<String, String> headers) {
        final String cookies = headers.get(COOKIE);

        if (cookies == null) {
            return HttpCookie.empty();
        }

        return HttpCookie.from(cookies);
    }

    private static Map<String, String> readBody(final InputStream inputStream,
                                                final Map<String, String> headers) throws IOException {
        final String contentLength = headers.get(CONTENT_LENGTH);

        if (contentLength == null) {
            return Map.of();
        }

        final int length = Integer.parseInt(contentLength);
        final byte[] body = inputStream.readNBytes(length);

        if (body.length != length) {
            throw new IllegalArgumentException("요청 본문이 Content-Length보다 짧습니다: " + contentLength);
        }

        return HttpRequestLine.parseParameters(new String(body, StandardCharsets.UTF_8));
    }

    public boolean isGet() {
        return "GET".equals(requestLine.method());
    }

    public boolean isPost() {
        return "POST".equals(requestLine.method());
    }
}
