package org.apache.coyote.http11.general;

import java.util.Map;

public class Cookies {

    private final Map<String, String> cookies;

    public Cookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String get(String key) {
        return this.cookies.get(key);
    }

    public boolean isEmpty() {
        return this.cookies.isEmpty();
    }
}
