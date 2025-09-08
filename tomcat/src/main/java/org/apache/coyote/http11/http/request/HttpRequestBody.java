package org.apache.coyote.http11.http.request;

import http.HttpHeaderKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpRequestBody {

    private final byte[] value;

    private HttpRequestBody(final byte[] value) {
        this.value = value;
    }

    public static HttpRequestBody of(final BufferedReader bufferedReader, final HttpHeader httpHeader)
            throws IOException {
        validateNull(bufferedReader, httpHeader);
        byte[] value = new byte[0];
        if (httpHeader.containsKey(HttpHeaderKey.CONTENT_LENGTH.getValue().toLowerCase())) {

            final int contentLength = Integer.parseInt(
                    httpHeader.getFirstValue(HttpHeaderKey.CONTENT_LENGTH.getValue().toLowerCase()));
            char[] bodyChars = new char[contentLength];
            int charsRead = bufferedReader.read(bodyChars, 0, contentLength);
            value = new String(bodyChars, 0, charsRead).getBytes(StandardCharsets.UTF_8);
        }
        return new HttpRequestBody(value);
    }

    private static void validateNull(final BufferedReader bufferedReader, final HttpHeader httpHeader) {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("bufferedReader는 null일 수 없습니다");
        }
        if (httpHeader == null) {
            throw new IllegalArgumentException("httpHeader는 null일 수 없습니다");
        }
    }

    public byte[] getValue() {
        return value;
    }
}
