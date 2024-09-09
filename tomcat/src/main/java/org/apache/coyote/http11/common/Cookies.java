package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private final Map<String, String> cookies;

    public Cookies() {
        this.cookies = new HashMap<>();
    }

    public String getCookieLine() {
        return String.join(
                "; ",
                cookies.entrySet()
                        .stream()
                        .map(cookiePair -> cookiePair.getKey() + "=" + cookiePair.getValue()).toArray(String[]::new)
        );
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    public boolean hasJSESSIONID() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSESSIONID() {
        return cookies.get("JSESSIONID");
    }
}
