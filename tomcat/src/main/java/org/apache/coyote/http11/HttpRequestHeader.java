package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.RequestCookie;

public class HttpRequestHeader {

    private final Map<String, String> values;
    private RequestCookie cookie;

    public HttpRequestHeader(Map<String, String> values) {
        this.values = values;
    }

    public HttpRequestHeader() {
        this.values = new LinkedHashMap<>();
    }

    public void add(String key, String value) {
        this.values.put(key, value);
    }

    public void addCookie(RequestCookie requestCookie) {
        this.cookie = requestCookie;
        this.values.remove("Cookie");
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public String get(String comparedKey) {
        return values.keySet().stream()
                .filter(key -> key.equals(comparedKey))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(comparedKey + "와 일치하는 값이 헤더에 존재하지 않습니다."));
    }

    public boolean hasCookie() {
        return this.values != null;
    }

    public RequestCookie getCookie() {
        return this.cookie;
    }
}
