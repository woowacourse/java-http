package org.apache.coyote.http11.message.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.cookie.ResponseCookie;

public class HttpResponseHeader {

    private final Map<String, String> values;
    private ResponseCookie cookie;

    public HttpResponseHeader() {
        this.values = new HashMap<>();
    }

    public void add(String key, String value) {
        this.values.put(key, value);
    }

    public void addCookie(ResponseCookie cookie) {
        this.cookie = cookie;
        this.values.remove("Cookie");
    }

    public String get(String comparedKey) {
        return values.keySet().stream()
                .filter(key -> key.equals(comparedKey))
                .findAny()
                .map(values::get)
                .orElseThrow(() -> new IllegalArgumentException(comparedKey + "와 일치하는 값이 헤더에 존재하지 않습니다."));
    }

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }

    public boolean hasCookie() {
        return this.cookie != null;
    }

    public ResponseCookie getCookie() {
        return this.cookie;
    }
}
