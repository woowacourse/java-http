package org.apache.coyote.http11.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(final InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        String requestLine = readLine(bufferedInputStream);
        if (requestLine == null) {
            return null;
        }

        RequestLine parsedRequestLine = parseRequestLine(requestLine);
        Map<String, String> headers = readHeaders(bufferedInputStream);
        String body = readBody(bufferedInputStream, parsedRequestLine.method(), headers);

        return new HttpRequest(
                parsedRequestLine.method(),
                parsedRequestLine.requestUri(),
                headers,
                body
        );
    }

    private static RequestLine parseRequestLine(final String requestLine) {
        String[] requestParts = requestLine.split("\\s+", 3);
        return new RequestLine(requestParts[0], requestParts[1]);
    }

    private static Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line = readLine(inputStream);
        while (line != null && !line.isEmpty()) {
            String[] header = line.split(": ", 2);
            headers.put(header[0], header[1]);
            line = readLine(inputStream);
        }
        return headers;
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            line.write(value);
        }

        if (value == -1 && line.size() == 0) {
            return null;
        }

        byte[] bytes = line.toByteArray();
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

        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        if (contentLength < 0) {
            throw new IOException("Content-Length는 음수일 수 없습니다.");
        }

        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new EOFException("요청 본문이 Content-Length보다 짧습니다.");
        }
        return new String(body, StandardCharsets.UTF_8);
    }
}
