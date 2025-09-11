package org.apache.coyote.http.response;

import static org.apache.coyote.http.common.HttpConstants.CONTENT_LENGTH_HEADER_NAME;
import static org.apache.coyote.http.common.HttpConstants.KEY_VALUE_SEPARATOR;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpConstants;
import org.apache.coyote.http.common.HttpHeader;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponseHeader {

    public static final String SET_COOKIE_HEADER_NAME = "Set-Cookie";

    private final HttpHeader header;

    public static HttpResponseHeader empty() {
        return new HttpResponseHeader(HttpHeader.empty());
    }

    public static HttpResponseHeader withContentType(final ContentType contentType) {
        final HttpHeader header = HttpHeader.empty();
        header.add(ContentType.HEADER_NAME, contentType.getMimeTypeAndCharset());
        return new HttpResponseHeader(header);
    }

    public void add(final String name, final String value) {
        header.add(name, value);
    }

    public void addSetCookie(final String name, final String value) {
        validateCookieName(name);
        validateCookieValue(value);
        header.add(SET_COOKIE_HEADER_NAME, name + KEY_VALUE_SEPARATOR + value);
    }

    public void setContentLength(final int contentLength) {
        header.add(HttpConstants.CONTENT_LENGTH_HEADER_NAME, String.valueOf(contentLength));
    }

    private void validateCookieName(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("쿠키 이름은 null이거나 비어있을 수 없습니다");
        }
        if (containsInvalidCookieCharacters(name)) {
            throw new IllegalArgumentException("쿠키 이름에 유효하지 않은 문자가 포함되어 있습니다: " + name);
        }
    }

    private void validateCookieValue(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("쿠키 값은 null일 수 없습니다");
        }
        if (containsInvalidCookieCharacters(value)) {
            throw new IllegalArgumentException("쿠키 값에 유효하지 않은 문자가 포함되어 있습니다: " + value);
        }
    }

    private boolean containsInvalidCookieCharacters(final String input) {
        // CR, LF, NULL 등 제어문자와 구분자 검사
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c < 0x20 || c == 0x7F ||  // 제어문자
                    c == '"' || c == ',' || c == ';' || c == '\\') {  // 쿠키 구분자
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return header.toString();
    }
}
