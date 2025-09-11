package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.domain.HttpCookies;
import org.apache.coyote.http11.domain.HttpMethod;

public record Http11Request(
        RequestLine requestLine,
        RequestHeaders headers,
        RequestBody body
) {

    public static Http11Request from(final BufferedReader bufferedReader)
            throws IOException {
        final String line = bufferedReader.readLine();
        final RequestLine requestLine = RequestLine.parse(line);

        final List<String> headers = extractHeaderLines(bufferedReader);
        final RequestHeaders requestHeaders = RequestHeaders.parse(headers);

        final int contentLength = requestHeaders.getContentLength();
        final String body = readBody(bufferedReader, contentLength);
        final RequestBody requestBody = RequestBody.parse(body);
        return new Http11Request(requestLine, requestHeaders, requestBody);
    }

    private static String readBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }

        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = bufferedReader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                break; // EOF 도달
            }
            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
    }

    private static List<String> extractHeaderLines(final BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .collect(Collectors.toList());
    }

    public boolean isCookiesEmpty() {
        final HttpCookies cookies = headers.getCookies();
        return cookies == null || cookies.isEmpty();
    }

    public String getJsessionid() {
        final HttpCookies cookies = headers.getCookies();
        if (cookies != null) {
            return cookies.getJsessionid();
        }
        return null;
    }

    public String parseResourcePath() {
        return requestLine.parseResourcePath();
    }

    public HttpMethod getMethod() {
        return requestLine.method();
    }

    public String getBodyValueByKey(String key) {
        return body.getValueByKey(key);
    }
}
