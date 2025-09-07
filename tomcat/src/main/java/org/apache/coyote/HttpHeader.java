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
        this.headers = new HashMap<>(headers);
    }

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public void set(String name, String value) {
        if(name == null || value == null){
            return;
        }
        final List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(name, values);
    }


    public void add(String name, String value) {
        if(name == null || value == null){
            return;
        }
        headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }

    public String getHeader(String name) {
        if(headers.get(name) == null){
            return null;
        }
        return headers.get(name).getFirst();
    }

    public Map<String, List<String>> getAllHeaders() {
        return headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));
    }

    public String getContentType() {
        return getHeader("Content-Type");
    }

    public String getCookie() {
        return getHeader("Cookie");
    }

    public String getContentLength() {
        return getHeader("Content-Length");
    }

    public void setContentType(String contentType) {
        set("Content-Type", contentType);
    }

    public void setContentLength(String contentLength) {
        set("Content-Length", contentLength);
    }

    public void setLocation(String location) {
        set("Location", location);
    }
}
