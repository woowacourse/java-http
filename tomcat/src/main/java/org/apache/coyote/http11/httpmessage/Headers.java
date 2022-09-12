package org.apache.coyote.http11.httpmessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.session.Cookie;

public class Headers {

    private final Map<String, Object> headers;

    public Headers(Map<String, Object> headers) {
        this.headers = headers;
    }

    public static Headers of(List<String> headerLines) {
        Map<String, Object> headers = new LinkedHashMap<>();

        for (String header : headerLines) {
            String[] keyValue = header.split(": ");
            headers.put(keyValue[0], keyValue[1].trim());
        }

        return new Headers(headers);
    }

    public void addContentType(ContentType contentType) {
        headers.put("Content-Type", contentType.getValue() + ";charset=utf-8");
    }

    public void addContentLength(int length) {
        headers.put("Content-Length", length);
    }

    public void addLocation(String path) {
        headers.put("Location", path);
    }

    public void addSetCookie(Cookie cookie) {
        headers.put("Set-Cookie", cookie);
    }

    public String getCookie() {
        return (String) headers.get("Cookie");
    }

    public Optional<Object> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Headers that = (Headers) o;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue().toString() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
