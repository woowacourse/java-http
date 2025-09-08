package org.apache.coyote.http11.http.request;

import http.HttpHeaderKey;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpRequestBody {

    private final byte[] value;

    private HttpRequestBody(final byte[] value) {
        this.value = value;
    }

    public static HttpRequestBody of(final InputStream inputStream, final HttpHeader httpHeader)
            throws IOException {
        validateNull(inputStream, httpHeader);

        Optional<String> contentLengthOpt = httpHeader.getFirstValue(
                HttpHeaderKey.CONTENT_LENGTH.getValue().toLowerCase());

        if (contentLengthOpt.isPresent()) {
            final int contentLength = Integer.parseInt(contentLengthOpt.get());
            if (contentLength > 0) {
                byte[] bodyBytes = inputStream.readNBytes(contentLength);
                return new HttpRequestBody(bodyBytes);
            }
        }
        return new HttpRequestBody(new byte[0]);
    }

    private static void validateNull(final InputStream inputStream, final HttpHeader httpHeader) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream은 null일 수 없습니다");
        }
        if (httpHeader == null) {
            throw new IllegalArgumentException("HttpHeader는 null일 수 없습니다");
        }
    }

    public byte[] getValue() {
        return value;
    }
}
