package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final var reader  = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));

        final var firstLine = reader.readLine();

        this.requestLine = RequestLine.parse(firstLine);
        this.headers = readHeaders(reader);
        this.body = readBody(reader);
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final var headers = new HashMap<String, String>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] tokens = line.trim().split(":", 2);

            if (tokens.length != 2) {
                throw new IllegalArgumentException("올바르지 않은 HTTP 헤더입니다.");
            }

            final var name = tokens[0].trim().toLowerCase(Locale.ROOT);
            final var value = tokens[1].trim();

            headers.put(name, value);
        }

        return headers;
    }

    private String readBody(final BufferedReader reader) throws IOException {
        final var contentLengthHeader = headers.get("content-length");

        if (contentLengthHeader == null) {
            if ("POST".equals(requestLine.getMethod())) {
                throw new IllegalArgumentException("Content-Length 헤더가 없습니다.");
            }

            return "";
        }

        final var contentLength = Integer.parseInt(contentLengthHeader);

        if (contentLength < 0) {
            throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다.");
        }

        final var buffer = new char[contentLength];

        int offset = 0;

        while (offset < contentLength) {
            final var readLength = reader.read(
                    buffer,
                    offset,
                    contentLength - offset
            );

            if (readLength == -1) {
                throw new IllegalArgumentException(
                        "요청 Body가 Content-Length보다 짧습니다."
                );
            }

            offset += readLength;
        }

        return new String(buffer);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getHeader(final String name) {
        return headers.get(name.trim().toLowerCase(Locale.ROOT));
    }

    public String getBody() {
        return body;
    }

    public String getPath() {
        final var uri = requestLine.getUri();
        final var queryIndex = uri.indexOf("?");

        if (queryIndex < 0) {
            return uri;
        }

        return uri.substring(0, queryIndex);
    }

}
