package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import javax.annotation.Nullable;
import org.apache.catalina.cookie.Cookie;
import org.apache.catalina.cookie.CookieUtils;

public class HttpRequestHeaders {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> headers;

    public HttpRequestHeaders(Map<String, String> headers) {
        this.headers = Map.copyOf(headers);
    }

    public HttpRequestHeaders() {
        this.headers = Collections.emptyMap();
    }

    @Nullable
    public String getContentType() {
        return headers.get(CONTENT_TYPE);
    }

    public Cookie getCookie() {
        return getHeader(COOKIE)
                .map(CookieUtils::createCookie)
                .orElse(new Cookie());
    }

    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpRequestHeaders that = (HttpRequestHeaders) object;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequestHeaders.class.getSimpleName() + "[", "]")
                .add("headers=" + headers)
                .toString();
    }
}
