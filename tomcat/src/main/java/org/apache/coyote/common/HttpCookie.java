package org.apache.coyote.common;

import java.util.Collections;
import java.util.Map;

public class HttpCookie {

    public static final HttpCookie EMPTY = new HttpCookie(Collections.emptyMap());

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String get(String name) {
        return cookies.getOrDefault(name, "");
    }
}
