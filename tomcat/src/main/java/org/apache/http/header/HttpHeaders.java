package org.apache.http.header;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.HttpCookie;

public class HttpHeaders {
    private final List<HttpHeader> headers;

    public HttpHeaders(HttpHeader... headers) {
        if (headers == null) {
            this.headers = new ArrayList<>();
            return;
        }

        this.headers = new ArrayList<>(Arrays.asList(headers));
    }

    public static HttpHeaders from(String[] headers) {
        return new HttpHeaders(Arrays.stream(headers)
                .map(HttpHeader::from)
                .toArray(HttpHeader[]::new));
    }

    public boolean existsHeader(HttpHeaderName httpHeaderName) {
        return headers.stream()
                .anyMatch(header -> header.getKey() == httpHeaderName);
    }

    public HttpCookie parseCookie() {
        return Optional.ofNullable(headers)
                .flatMap(hs -> headers.stream()
                        .filter(header -> HttpHeaderName.COOKIE == header.getKey())
                        .findFirst()
                        .map(header -> HttpCookie.from(header.getValue())))
                .orElse(null);
    }

    public HttpCookie getCookie() {
        HttpHeader cookieHeader = headers.stream()
                .filter(header -> header.getKey() == HttpHeaderName.COOKIE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("쿠키가 존재하지 않습니다."));

        return HttpCookie.from(cookieHeader.getValue());
    }

    public HttpCookie getSetCookie() {
        HttpHeader cookieHeader = headers.stream()
                .filter(header -> header.getKey() == HttpHeaderName.SET_COOKIE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Set-Cookie가 존재하지 않습니다."));

        return HttpCookie.from(cookieHeader.getValue());
    }

    public HttpHeader getHeader(final HttpHeaderName httpHeaderName) {
        return headers.stream()
                .filter(header -> header.getKey() == httpHeaderName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 헤더가 존재하지 않습니다."));
    }

    public List<HttpHeader> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach(header -> stringBuilder.append(header.toString()).append("\r\n"));
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
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(headers);
    }
}
