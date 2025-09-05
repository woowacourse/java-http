package org.apache.coyote.http11.http.request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.http.common.header.HttpHeader;
import org.apache.coyote.http11.http.common.header.HttpHeaderKey;

public class HttpRequestBody {

    private final String value;

    private HttpRequestBody(final InputStream inputStream, final HttpHeader httpHeader) throws IOException {
        if (!httpHeader.containsKey(HttpHeaderKey.CONTENT_LENGTH.getValue())) {
            this.value = null;
            return;
        }
        final int contentLength = Integer.parseInt(httpHeader.getValue(HttpHeaderKey.CONTENT_LENGTH.getValue()));
        this.value = new String(inputStream.readNBytes(contentLength), StandardCharsets.UTF_8);
    }

    public static HttpRequestBody of(final InputStream inputStream, final HttpHeader httpHeader) throws IOException {
        validateNull(inputStream, httpHeader);
        return new HttpRequestBody(inputStream, httpHeader);
    }

    private static void validateNull(final InputStream inputStream, final HttpHeader httpHeader) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStraem은 null일 수 없습니다");
        }
        if (httpHeader == null) {
            throw new IllegalArgumentException("httpHeader는 null일 수 없습니다");
        }
    }

    public String getValue() {
        return value;
    }
}
