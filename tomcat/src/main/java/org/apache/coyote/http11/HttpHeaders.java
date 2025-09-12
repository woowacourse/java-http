package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(final Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public List<String> getValues(final String name) {
        return headers.getOrDefault(name, List.of());
    }

    public Optional<String> get(final String name) {
        List<String> values = getValues(name);
        return values.isEmpty() ? Optional.empty() : Optional.of(values.getFirst());
    }

    public Map<String, List<String>> getHeaders() {
        return new LinkedHashMap<>(headers);
    }

    public String getCookieHeader() {
        return get("Cookie").orElse("");
    }

    public void setHeader(final String name, final String value) {
        if (headers.containsKey(name)) {
            List<String> values = new LinkedList<>(headers.get(name));
            values.add(value);
            headers.put(name, new ArrayList<>(values));
            return;
        }
        headers.put(name, List.of(value));
    }

    public void setContentLength(final int length) {
        headers.put("Content-Length", List.of(String.valueOf(length)));
    }

    public void setContentType(final MimeType contentType) {
        headers.put("Content-Type", List.of(contentType.getType()));
    }
}
