package org.apache.coyote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpHeader {

    private final Map<String, List<String>> headers;

    public HttpHeader(Map<String, List<String>> headers) {
        this.headers = headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));
    }

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public void set(String name, String value) {
        if(name == null || value == null){
            return;
        }
        final String normalizedName = name.toLowerCase();
        final List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(normalizedName, values);
    }

    public void add(String name, String value) {
        if(name == null || value == null){
            return;
        }
        String normalizedName = name.toLowerCase();
        headers.computeIfAbsent(normalizedName, key -> new ArrayList<>()).add(value);
    }

    public String getHeader(String name) {
        final String normalizedName = name.toLowerCase();
        if(headers.get(normalizedName) == null){
            return null;
        }
        return headers.get(normalizedName).getFirst();
    }

    public Map<String, List<String>> getAllHeaders() {
        return headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));
    }

    public String getContentType() {
        return getHeader("content-type");
    }

    public String getCookie() {
        return getHeader("cookie");
    }

    public String getContentLength() {
        return getHeader("content-length");
    }

    public void setContentType(String contentType) {
        set("content-type", contentType);
    }

    public void setContentLength(String contentLength) {
        set("content-length", contentLength);
    }

    public void setLocation(String location) {
        set("location", location);
    }
}
