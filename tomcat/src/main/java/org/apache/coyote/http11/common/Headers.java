package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.session.HttpCookie;

public class Headers {

    private static final String COOKIE = "Cookie";
    private static final int DEFAULT_HEADER_FIELD_LENGTH = 2;

    private final Map<String, String> values;
    private HttpCookie httpCookie;

    public Headers() {
        this.values = new HashMap<>();
        this.httpCookie = new HttpCookie();
    }

    public void add(final String header) {
        final String[] field = header.split(": ");
        if (field.length != DEFAULT_HEADER_FIELD_LENGTH) {
            throw new IllegalArgumentException("올바른 Header Field 형식이 아닙니다.");
        }
        if (field[0].equals(COOKIE)) {
            this.httpCookie = HttpCookie.from(field[1]);
        }
        values.put(field[0].trim(), field[1].trim());
    }

    public Object findField(final String fieldName) {
        return Optional.ofNullable(values.get(fieldName))
                .orElseThrow(() -> new NoSuchElementException("Header Field에 key 값이 존재하지 않습니다."));
    }

    public Optional<String> findCookie(final String name) {
        final Optional<String> cookies = Optional.ofNullable(values.get(COOKIE));
        if (cookies.isEmpty()) {
            return Optional.empty();
        }

        return httpCookie.findCookie(name);
    }

    public Map<String, String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\r\n"));
    }

    public void setContentType(final ContentType contentType) {
        values.put("Content-Type", contentType.getValue() + ";charset=utf-8");
    }

    public void setContentLength(final String contents) {
        values.put("Content-Length", String.valueOf(contents.getBytes().length));
    }

    public void setLocation(final String location) {
        values.put("Location", location);
    }

    public void setCookie(final String cookie) {
        values.put("Set-Cookie", "JSESSIONID=" + cookie);
    }
}
