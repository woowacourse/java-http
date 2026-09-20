package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    // 요청과 응답에 모두 사용할 수 있는 클래스
    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
