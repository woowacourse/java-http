package org.apache.coyote.http11;

import org.apache.coyote.http11.body.Body;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public record HttpRequest(
        RequestLine requestLine,
        Headers headers,
        Body body
) {
    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final var lines = read(bufferedReader);
        final var requestLine = RequestLine.parse(lines.get(0));
        final var headers = Headers.parse(lines.subList(1, lines.size()));
        final var body = Body.parse(headers.getContentLength(), headers.getContentType(), bufferedReader);
        return new HttpRequest(requestLine, headers, body);
    }

    private static List<String> read(final BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .toList();
    }
}
