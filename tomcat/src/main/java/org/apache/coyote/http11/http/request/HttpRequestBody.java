package org.apache.coyote.http11.http.request;

import http.HttpHeaderKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpRequestBody {

    private final String value;

    private HttpRequestBody(final String value) {
        this.value = value;
    }

    public static HttpRequestBody of(final InputStream inputStream, final HttpHeader httpHeader) throws IOException {
        validateNull(inputStream, httpHeader);
        String value = null;
        if (httpHeader.containsKey(HttpHeaderKey.CONTENT_LENGTH.getValue())) {
            final int contentLength = Integer.parseInt(httpHeader.getValue(HttpHeaderKey.CONTENT_LENGTH.getValue()));
            value = new String(inputStream.readNBytes(contentLength), StandardCharsets.UTF_8);
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

    public String getValue() {
        return value;
    }
}
