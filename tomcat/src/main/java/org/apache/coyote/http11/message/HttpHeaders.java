package org.apache.coyote.http11.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders(Map<String, List<String>> headers) {
        this.headers = new HashMap<>(headers);
    }

    public HttpHeaders() {
        this(new HashMap<>());
    }

    public boolean isHeader(HttpHeaderName headerName, String headerValue) {
        return headerValue.equals(getFieldByHeaderName(headerName).orElse(""));
    }

    public boolean hasHeader(String header) {
        return headers.containsKey(header);
    }

    public Optional<String> getFieldByHeaderName(HttpHeaderName headerName) {
        List<String> fields = Optional.ofNullable(headers.get(headerName.getName()))
                .orElse(new ArrayList<>());

        if (fields.size() > 1) {
            throw new IllegalArgumentException("헤더의 필드가 1개보다 많습니다.");
        }

        return fields.stream()
                .findFirst();
    }

    public void setHeader(HttpHeaderName name, String field) {
        headers.computeIfAbsent(name.getName(), key -> new ArrayList<>())
                .add(field);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(headers.getOrDefault(HttpHeaderName.COOKIE.getName(), new ArrayList<>()));
    }

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}