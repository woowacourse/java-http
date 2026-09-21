package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

record HttpRequest(HttpRequestLine requestLine, HttpHeaders headers, String body) {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int END_OF_STREAM = -1;

    static Optional<HttpRequest> readFrom(final BufferedReader reader) throws IOException {
        final var requestLine = readRequestLine(reader);
        if (requestLine.isEmpty()) {
            return Optional.empty();
        }

        final var headers = readHeaders(reader);
        if (headers.isEmpty()) {
            return Optional.empty();
        }

        final var body = readBody(reader, headers.get());
        if (body.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new HttpRequest(requestLine.get(), headers.get(), body.get()));
    }

    String method() {
        return requestLine.method();
    }

    URI uri() {
        return requestLine.uri();
    }

    private static Optional<HttpRequestLine> readRequestLine(final BufferedReader reader) throws IOException {
        final var value = reader.readLine();
        if (value == null) {
            return Optional.empty();
        }
        return HttpRequestLine.parse(value);
    }

    private static Optional<HttpHeaders> readHeaders(final BufferedReader reader) throws IOException {
        var headers = HttpHeaders.empty();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                return Optional.of(headers);
            }

            final var header = HttpHeader.parse(line);
            if (header.isEmpty()) {
                return Optional.empty();
            }
            headers = headers.add(header.get());
        }
        return Optional.empty();
    }

    private static Optional<String> readBody(
            final BufferedReader reader,
            final HttpHeaders headers
    ) throws IOException {
        final var contentLengthValue = headers.firstValue(CONTENT_LENGTH);
        if (contentLengthValue.isEmpty()) {
            return Optional.of("");
        }

        final int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthValue.get());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        if (contentLength < 0) {
            return Optional.empty();
        }

        final var buffer = new char[contentLength];
        var offset = 0;
        while (offset < contentLength) {
            final int readCount = reader.read(buffer, offset, contentLength - offset);
            if (readCount == END_OF_STREAM) {
                return Optional.empty();
            }
            offset += readCount;
        }
        return Optional.of(new String(buffer));
    }
}
