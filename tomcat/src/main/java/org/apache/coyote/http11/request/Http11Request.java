package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.catalina.exception.Http4xxException;
import org.apache.coyote.http11.domain.HttpCookies;
import org.apache.coyote.http11.domain.HttpMethod;

public record Http11Request(
        RequestLine requestLine,
        RequestHeaders headers,
        RequestBody body
) {

    public static Http11Request from(final InputStream inputStream) throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                throw new Http4xxException();
            }
            final RequestLine requestLine = RequestLine.parse(line);

            final List<String> headers = extractHeaderLines(bufferedReader);
            final RequestHeaders requestHeaders = RequestHeaders.parse(headers);

            final int contentLength = requestHeaders.getContentLength();
            final String body = readBody(inputStream, contentLength);
            final RequestBody requestBody = RequestBody.parse(body);

            return new Http11Request(requestLine, requestHeaders, requestBody);
        }
    }

    private static String readBody(final InputStream inputStream, final int contentLength) throws IOException {
        byte[] bytes = inputStream.readNBytes(contentLength);
        return new String(bytes);
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
