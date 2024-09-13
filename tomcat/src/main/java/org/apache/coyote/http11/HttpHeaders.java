package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> fields;

    public HttpHeaders() {
        this.fields = new LinkedHashMap<>();
    }

    public void add(String headerLine) {
        String[] parts = headerLine.split(": ", 2);
        add(parts[0], parts[1]);
    }

    public void add(String name, String value) {
        fields.put(name, value);
    }

    public void add(HeaderName headerName, String value) {
        add(headerName.getValue(), value);
    }

    public Optional<String> findByName(HeaderName headerName) {
        return fields.entrySet().stream()
                .filter(entry -> entry.getKey().equals(headerName.getValue()))
                .map(Entry::getValue)
                .findAny();
    }

    public HttpCookies getCookies() {
        Optional<String> value = findByName(HeaderName.COOKIE);
        return value.map(HttpCookies::new).orElseGet(HttpCookies::new);
    }

    public int getContentLength() {
        return Integer.parseInt(findByName(HeaderName.CONTENT_LENGTH).orElse("0"));
    }

    public String render() {
        return fields.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(" \r\n", "", " "));
    }

    @Override
    public String toString() {
        return render();
    }
}
