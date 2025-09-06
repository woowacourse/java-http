package org.apache.coyote.http11.http.request;

import http.HttpHeaderKey;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpRequestBody {

    private final byte[] value;

    private HttpRequestBody(final byte[] value) {
        this.value = value;
    }

    public static HttpRequestBody of(final InputStream inputStream, final HttpHeader httpHeader) throws IOException {
        validateNull(inputStream, httpHeader);
        byte[] value = new byte[0];
        if (httpHeader.containsKey(HttpHeaderKey.CONTENT_LENGTH.getValue())) {
            final int contentLength = Integer.parseInt(httpHeader.getValue(HttpHeaderKey.CONTENT_LENGTH.getValue()));
            value = inputStream.readNBytes(contentLength);
        }
        return new HttpRequestBody(value);
    }

    private static void validateNull(final InputStream inputStream, final HttpHeader httpHeader) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream은 null일 수 없습니다");
        }
        if (httpHeader == null) {
            throw new IllegalArgumentException("httpHeader는 null일 수 없습니다");
        }
    }

    public byte[] getValue() {
        return value;
    }
}
