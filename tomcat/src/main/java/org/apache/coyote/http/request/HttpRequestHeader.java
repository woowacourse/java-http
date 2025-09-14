package org.apache.coyote.http.request;

import static common.HttpConstants.COOKIE_HEADER_NAME;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import common.ContentType;
import org.apache.coyote.http.HttpHeader;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestHeader {

    private final HttpHeader header;
    private final HttpCookie cookie;

    public static HttpRequestHeader from(final String rawRequestHeader) {
        if (rawRequestHeader == null || rawRequestHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP 요청 헤더는 null이거나 비어있을 수 없습니다");
        }

        final HttpHeader header = HttpHeader.from(rawRequestHeader);
        final HttpCookie cookie = HttpCookie.from(header.get(COOKIE_HEADER_NAME));

        return new HttpRequestHeader(header, cookie);
    }

    public String get(final String name) {
        return header.get(name);
    }

    public String getCookie(final String name) {
        return cookie.get(name);
    }

    public ContentType getContentType() {
        return header.getContentType();
    }

    public int getContentLength() {
        return header.getContentLength();
    }

    public Map<String, String> asMap() {
        return header.asMap();
    }

    @Override
    public String toString() {
        return "%s%s".formatted(header, cookie);
    }
}
