package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(final BufferedReader reader) {
        requestLine = resolveRequestLine(reader);
        headers = new RequestHeaders(reader);
        body = resolveBody(reader);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(final String name) {
        return headers.getHeader(name);
    }

    public RequestBody getBody() {
        return body;
    }

    private RequestLine resolveRequestLine(final BufferedReader reader) {
        try {
            final String line = reader.readLine();

            if (line == null || line.isBlank()) {
                throw new IllegalArgumentException("Request Line이 존재하지 않습니다.");
            }

            return new RequestLine(line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RequestBody resolveBody(final BufferedReader reader) {
        final int contentLength = headers.getContentLength();

        if (contentLength == 0) {
            return new RequestBody("");
        }

        final char[] buffer = new char[contentLength];

        try {
            int offset = 0;

            while (offset < contentLength) {
                final int read = reader.read(
                        buffer,
                        offset,
                        contentLength - offset
                );

                if (read == -1) {
                    throw new IllegalArgumentException("Content-Length보다 Body가 짧습니다.");
                }

                offset += read;
            }

            return new RequestBody(new String(buffer));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

