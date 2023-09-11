package org.apache.coyote.http11;

import org.apache.coyote.http11.body.Body;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.HttpMethod;
import org.apache.coyote.http11.header.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(final RequestLine requestLine, final Headers headers, final Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

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
                .collect(Collectors.toList());
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }

    public boolean isGet() {
        return requestLine.getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return requestLine.getMethod() == HttpMethod.POST;
    }

    public String getPath() {
        return requestLine.getUri().getPath();
    }
}
