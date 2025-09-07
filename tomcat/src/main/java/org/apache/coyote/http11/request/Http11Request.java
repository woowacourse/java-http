package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Http11Request(
        RequestLine requestLine,
        RequestHeaders headers,
        RequestBody body
) {

    public Http11Request(String requestLine, List<String> headers, byte[] body) {
        this(RequestLine.parse(requestLine), RequestHeaders.parse(headers), RequestBody.parse(body));
    }

    public static Http11Request from(BufferedReader bufferedReader) throws IOException {
        final var requestLine = bufferedReader.readLine();
        final var headers = extractHeaderLines(bufferedReader);
        return new Http11Request(requestLine, headers, null);
    }

    private static List<String> extractHeaderLines(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .collect(Collectors.toList());
    }

    public String parseResourcePath() {
        return requestLine.parseResourcePath();
    }

    public Map<String, String> parseQuery() {
        return requestLine.parseQuery();
    }

    public String getMethod() {
        return requestLine.method();
    }

    public String getRequestTarget() {
        return requestLine.requestTarget();
    }
}
