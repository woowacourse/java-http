package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public record HttpRequest(
        RequestLine requestLine,
        HttpHeaders headers,
        RequestBody body
) {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedInputStream reader = new BufferedInputStream(inputStream);

        String requestLine = readLine(reader);
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 없습니다.");
        }

        RequestLine parsedRequestLine = RequestLine.parse(requestLine);
        HttpHeaders headers = readHeaders(reader);
        RequestBody body = readBody(reader, headers);

        return new HttpRequest(parsedRequestLine, headers, body);
    }

    private static HttpHeaders readHeaders(InputStream inputStream) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String line;

        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            headers.add(line);
        }

        return headers;
    }

    private static RequestBody readBody(InputStream inputStream, HttpHeaders headers) throws IOException {
        int contentLength = headers.contentLength();
        byte[] body = inputStream.readNBytes(contentLength);

        if (body.length != contentLength) {
            throw new IOException("요청 body가 예상된 값보다 짧습니다.");
        }

        return new RequestBody(new String(body, StandardCharsets.UTF_8));
    }

    private static String readLine(InputStream inputStream) throws IOException {
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

        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }
}
