package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(
            RequestLine requestLine,
            Map<String, String> headers,
            String body
    ) {
        this.requestLine = Objects.requireNonNull(requestLine);
        this.headers = Map.copyOf(headers);
        this.body = Objects.requireNonNull(body);
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        RequestLine requestLine = RequestLine.parse(reader.readLine());
        Map<String, String> headers = readHeaders(reader);
        String body = readBody(reader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] pair = line.split(":", 2);

            if (pair.length == 2) {
                headers.put(pair[0].trim().toLowerCase(Locale.ROOT), pair[1].trim());
            }
        }

        return headers;
    }

    private static String readBody(
            BufferedReader reader,
            Map<String, String> headers
    ) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int readCount = reader.read(
                    buffer,
                    totalRead,
                    contentLength - totalRead
            );

            if (readCount == -1) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }

            totalRead += readCount;
        }

        return new String(buffer);
    }
}
