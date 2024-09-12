package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import org.apache.catalina.cookie.Cookie;
import org.apache.catalina.cookie.CookieUtils;
import org.apache.catalina.session.Session;

public class HttpResponseHeaders {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    public HttpResponseHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public HttpResponseHeaders() {
        this.headers = new HashMap<>();
    }

    public void addContentType(String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void addContentLength(long length) {
        headers.put(CONTENT_LENGTH, Long.toString(length));
    }

    public void addLocation(String path) {
        headers.put(LOCATION, path);
    }

    public void setSession(Session session) {
        Cookie cookie = CookieUtils.createCookie(headers.get(SET_COOKIE));
        cookie.setSession(session);
        headers.put(SET_COOKIE, CookieUtils.toValues(cookie));
    }

    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpResponseHeaders that = (HttpResponseHeaders) object;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpResponseHeaders.class.getSimpleName() + "[", "]")
                .add("headers=" + headers)
                .toString();
    }
}
