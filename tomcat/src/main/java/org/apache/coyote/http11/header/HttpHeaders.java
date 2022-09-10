package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.cookie.SetCookieStringBuilder;

public class HttpHeaders {

    public static final String LOCATION = "Location";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final List<HttpHeader> headers;

    public HttpHeaders(final List<HttpHeader> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(final List<String> rawHeaders) {
        final List<HttpHeader> headers = rawHeaders.stream()
                .map(rawHeader -> {
                    final String[] headerKeyValueSplit = rawHeader.split(KEY_VALUE_SEPARATOR);
                    final String headerKey = headerKeyValueSplit[KEY_INDEX];
                    final String headerValue = headerKeyValueSplit[VALUE_INDEX].trim();
                    return new HttpHeader(headerKey, headerValue);
                })
                .collect(Collectors.toList());
        return new HttpHeaders(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new ArrayList<>());
    }

    public List<HttpHeader> findHeadersByKey(final String key) {
        return headers.stream()
                .filter(header -> header.getKey().equals(key))
                .collect(Collectors.toList());
    }

    public Optional<HttpHeader> findOneHeaderByKey(final String key) {
        final List<HttpHeader> headers = findHeadersByKey(key);
        if (headers.isEmpty()) {
            return Optional.empty();
        }
        if (headers.size() > 1) {
            throw new RuntimeException(String.format("해당 헤더 키로 한개 이상의 헤더가 존재합니다. %s", key));
        }
        return Optional.of(headers.get(0));
    }

    public String toHttpMessageHeader() {
        return headers.stream()
                .map(header -> header.getKey() + KEY_VALUE_SEPARATOR + header.getValue() + " ")
                .collect(Collectors.joining(CRLF));
    }

    public Cookies getCookies() {
        return findOneHeaderByKey(HttpHeaders.COOKIE)
                .map(header -> Cookies.from(header.getValue()))
                .orElse(Cookies.empty());
    }

    public boolean hasSession() {
        return getCookies()
                .getSessionId()
                .isPresent();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpHeaders)) {
            return false;
        }
        final HttpHeaders that = (HttpHeaders) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    public void setSession(final String sessionId, final int maxAgeSecond) {
        final SetCookieStringBuilder setCookieStringBuilder
                = new SetCookieStringBuilder(new Cookie(Cookies.SESSION_KEY, sessionId));
        setCookieStringBuilder.setMaxAge(maxAgeSecond);
        setCookieStringBuilder.setHttpOnly(true);
        headers.add(new HttpHeader(SET_COOKIE, setCookieStringBuilder.toString()));
    }
}
