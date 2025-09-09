package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.domain.HttpMethod;

public record Http11Request(
        RequestLine requestLine,
        RequestHeaders headers,
        RequestBody body
) {

    public static Http11Request from(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        final RequestLine requestLine = RequestLine.parse(line);

        final List<String> headers = extractHeaderLines(bufferedReader);
        final RequestHeaders requestHeaders = RequestHeaders.parse(headers);

        final int contentLength = requestHeaders.getContentLength();
        final String body = readBody(bufferedReader, contentLength);
        final RequestBody requestBody = RequestBody.parse(body);

        return new Http11Request(requestLine, requestHeaders, requestBody);
    }

    private static String readBody(final BufferedReader bufferedReader, final int length) throws IOException {
        if (length <= 0) {
            return "";
        }
        char[] bodyChars = new char[length];
        bufferedReader.read(bodyChars, 0, length);
        return new String(bodyChars);
    }

    private static List<String> extractHeaderLines(final BufferedReader bufferedReader) {
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

    public HttpMethod getMethod() {
        return requestLine.method();
    }

    public String getRequestTarget() {
        return requestLine.requestTarget();
    }

    public boolean isCookiesEmpty() {
        return headers.getCookies() == null;
    }
}
