package org.apache.http.header;

import java.util.Arrays;
import java.util.Objects;

import org.apache.http.HttpCookie;

public class HttpHeaders {
    private final HttpHeader[] headers;

    public HttpHeaders(HttpHeader... headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(String[] headers) {
        return new HttpHeaders(Arrays.stream(headers)
                .map(HttpHeader::from)
                .toArray(HttpHeader[]::new));
    }

    public boolean existsHeader(HttpHeaderName httpHeaderName) {
        return Arrays.stream(headers)
                .anyMatch(header -> header.getKey() == httpHeaderName);
    }

    public HttpCookie getCookie() {
        HttpHeader cookieHeader = Arrays.stream(headers)
                .filter(header -> header.getKey() == HttpHeaderName.COOKIE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("쿠키가 존재하지 않습니다."));

        return HttpCookie.from(cookieHeader.getValue());
    }

    public HttpCookie getSetCookie() {
        HttpHeader cookieHeader = Arrays.stream(headers)
                .filter(header -> header.getKey() == HttpHeaderName.SET_COOKIE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Set-Cookie가 존재하지 않습니다."));

        return HttpCookie.from(cookieHeader.getValue());
    }

    public HttpHeader getHeader(final HttpHeaderName httpHeaderName) {
        return Arrays.stream(headers)
                .filter(header -> header.getKey() == httpHeaderName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 헤더가 존재하지 않습니다."));
    }

    public HttpHeader[] getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(headers)
                .forEach(header -> stringBuilder.append(header.toString()).append("\r\n"));
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpHeaders that = (HttpHeaders) o;
        return Objects.deepEquals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(headers);
    }
}
