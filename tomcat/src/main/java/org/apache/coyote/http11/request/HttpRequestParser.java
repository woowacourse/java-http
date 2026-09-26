package org.apache.coyote.http11.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        final String requestLine = readLine(bufferedInputStream);
        if (requestLine == null) {
            return null;
        }

        final RequestLine parsedRequestLine = parseRequestLine(requestLine);
        final Map<String, String> headers = readHeaders(bufferedInputStream);
        final String body = readBody(bufferedInputStream, parsedRequestLine.method(), headers);

        return new HttpRequest(
                parsedRequestLine,
                headers,
                body
        );
    }

    private static RequestLine parseRequestLine(final String requestLine) {
        final String[] requestParts = requestLine.split("\\s+", 3);
        return new RequestLine(requestParts[0], requestParts[1]);
    }

    private static Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        String line = readLine(inputStream);
        while (line != null && !line.isEmpty()) {
            final int separator = line.indexOf(':');
            if (separator <= 0) {
                throw new IOException("올바르지 않은 HTTP 헤더입니다: " + line);
            }
            headers.put(line.substring(0, separator), line.substring(separator + 1).stripLeading());
            line = readLine(inputStream);
        }
        return headers;
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            line.write(value);
        }

        if (value == -1 && line.size() == 0) {
            return null;
        }

        final byte[] bytes = line.toByteArray();
        int length = bytes.length;
        if (length > 0 && bytes[length - 1] == '\r') {
            length--;
        }
        return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
    }

    private static String readBody(final InputStream inputStream, final String method,
                                   final Map<String, String> headers) throws IOException {
        if (!"POST".equals(method)) {
            return "";
        }

        final String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        if (contentLength < 0) {
            throw new IOException("Content-Length는 음수일 수 없습니다.");
        }

        final byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new EOFException("요청 본문이 Content-Length보다 짧습니다.");
        }
        return new String(body, StandardCharsets.UTF_8);
    }
}
