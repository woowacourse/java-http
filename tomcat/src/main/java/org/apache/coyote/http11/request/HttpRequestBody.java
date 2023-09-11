package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestBody {
    private final String body;

    public static HttpRequestBody from(final String contentLength, final BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            throw new IllegalArgumentException("Content-Length 값이 올바르지 않습니다.");
        }
        int length = Integer.parseInt(contentLength.trim());
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new HttpRequestBody(new String(buffer));
    }

    private HttpRequestBody(final String body) {
        this.body = body;
    }

    public String[] split(final String regex) {
        return body.split(regex);
    }
}
