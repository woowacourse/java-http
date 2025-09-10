package org.apache.coyote.http11;

public class HttpCookie {

    private final String key;
    private final String value;

    public HttpCookie(String key, String value) {
        if (key == null || key.isBlank() || value == null || value.isBlank()) {
            throw new IllegalArgumentException("key, value는 비어있을 수 없습니다.");
        }
        this.key = key;
        this.value = value;
    }

    public boolean isKey(String key) {
        return this.key.equals(key);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
