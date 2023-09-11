package org.apache.coyote.http11.body;

import org.apache.coyote.http11.header.ContentType;

import java.io.BufferedReader;
import java.io.IOException;

public interface Body {
    static Body parse(int contentLength, ContentType contentType, final BufferedReader bufferedReader) throws IOException {
        final char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);
        return switch (contentType) {
            case FORM_URLENCODED -> FormUrlEncodedBody.parse(String.valueOf(body));
            case NONE -> new EmptyBody();
            default -> throw new IllegalArgumentException("지원하지 않는 Content-Type 입니다.");
        };
    }

    String getValue(String key);
}
