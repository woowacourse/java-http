package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public record HttpRequest(
        RequestLine requestLine,
        HttpHeaders headers,
        RequestBody body
) {

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 없습니다.");
        }

        RequestLine parsedRequestLine = RequestLine.parse(requestLine);
        HttpHeaders headers = readHeaders(reader);
        RequestBody body = readBody(reader, headers);

        return new HttpRequest(parsedRequestLine, headers, body);
    }

    private static HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headers.add(line);
        }

        return headers;
    }

    private static RequestBody readBody(BufferedReader reader, HttpHeaders headers) throws IOException {
        int contentLength = headers.contentLength();
        char[] body = new char[contentLength];
        int current = 0;

        while (current < contentLength) {
            int read = reader.read(body, current, contentLength - current);

            if (read == -1) {
                throw new IOException("요청 body가 예상된 값보다 짧습니다.");
            }

            current += read;
        }

        return new RequestBody(new String(body));
    }
}
