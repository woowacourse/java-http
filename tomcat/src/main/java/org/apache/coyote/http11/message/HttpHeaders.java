package org.apache.coyote.http11.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpHeaders {
    private final Map<String, List<String>> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public List<String> getHeaders(String name) {
        return headers.getOrDefault(name, Collections.emptyList());
    }

    public Set<String> keySet() {
        return headers.keySet();
    }

    public String getContentLength() {
        final List<String> values = headers.get("Content-Length");
        if (values == null || values.isEmpty()) {
            return "0";
        }
        return values.get(0);
    }

    public String getFirst(final String name) {
        final List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.getFirst();
    }
}
