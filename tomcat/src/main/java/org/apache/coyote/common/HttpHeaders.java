package org.apache.coyote.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String key, String value) {
        List<String> header = headers.computeIfAbsent(key, ignore -> new ArrayList<>());
        header.add(value);
    }

    public void addHeader(String key, List<String> values) {
        List<String> header = headers.computeIfAbsent(key, ignore -> new ArrayList<>());
        header.addAll(values);
    }

    public void setHeader(String key, String value) {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        headers.put(key, values);
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void setHeader(String key, List<String> values) {
        headers.put(key, values);
    }

    public List<String> getHeader(String key) {
        return headers.getOrDefault(key, Collections.emptyList());
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
