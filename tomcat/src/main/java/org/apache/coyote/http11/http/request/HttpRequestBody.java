package org.apache.coyote.http11.http.request;

import http.HttpHeaderKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpRequestBody {

    private final byte[] value;

    private HttpRequestBody(final byte[] value) {
        this.value = value;
    }

    public static HttpRequestBody of(final BufferedReader bufferedReader, final HttpHeader httpHeader)
            throws IOException {
        validateNotNull(bufferedReader, httpHeader);

        Optional<String> contentLengthOpt = httpHeader.getFirstValue(
                HttpHeaderKey.CONTENT_LENGTH.getValue().toLowerCase());

        byte[] body = new byte[0];

        if (contentLengthOpt.isPresent()) {
            final int contentLength = Integer.parseInt(contentLengthOpt.get());
            if (contentLength > 0) {
                char[] bodyReads = new char[contentLength];

                int readLength = bufferedReader.read(bodyReads, 0, contentLength);

                if (readLength > 0) {
                    body = new String(bodyReads, 0, readLength).getBytes(StandardCharsets.UTF_8);
                }
            }
        }
        return new HttpRequestBody(body);
    }

    private static void validateNotNull(final BufferedReader bufferedReader, final HttpHeader httpHeader) {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("BufferedReader는 null일 수 없습니다");
        }
        if (httpHeader == null) {
            throw new IllegalArgumentException("HttpHeader는 null일 수 없습니다");
        }
    }

    public Map<String, String> getBodyElement() {
        String bodyLine = new String(value, StandardCharsets.UTF_8);
        return parseBodyValue(bodyLine);
    }

    private Map<String, String> parseBodyValue(final String target) {
        final Map<String, String> bodyValue = new HashMap<>();
        final String[] elements = target.split("&");

        for (String element : elements) {
            final String[] values = element.split("=");
            final String key = URLDecoder.decode(values[0], StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(values[1], StandardCharsets.UTF_8);
            bodyValue.put(key, value);
        }

        return bodyValue;
    }

    public byte[] getValue() {
        return value;
    }
}
