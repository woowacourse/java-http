package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private final Map<String, String> cookies;

    public Cookies() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(String cookieLine) {
        String[] pairs = cookieLine.split("=");

        String name = pairs[0];
        String value = pairs[1];

        cookies.put(name, value);
    }

    public String getCookieLine() {
        return String.join(
                "; ",
                cookies.entrySet()
                        .stream()
                        .map(cookiePair -> cookiePair.getKey() + "=" + cookiePair.getValue()).toArray(String[]::new)
        );
    }

    public boolean hasJSESSIONID() {
        return cookies.containsKey("JSESSIONID");
    }
}
