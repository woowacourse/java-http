package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.util.Symbol;

public class Cookies {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private final Map<String, String> cookies = new HashMap<>();

    public String getCookieLine() {
        return String.join(
                Symbol.SEMICOLON + Symbol.SPACE,
                cookies.entrySet()
                        .stream()
                        .map(cookiePair -> cookiePair.getKey() + Symbol.QUERY_PARAM_DELIMITER + cookiePair.getValue())
                        .toArray(String[]::new)
        );
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    public boolean hasCookies() {
        return !cookies.isEmpty();
    }

    public boolean hasJSESSIONID() {
        return cookies.containsKey(JSESSIONID_KEY);
    }

    public String getJSESSIONID() {
        return cookies.get(JSESSIONID_KEY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cookies cookies1)) {
            return false;
        }
        return Objects.equals(cookies, cookies1.cookies);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cookies);
    }
}
