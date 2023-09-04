package org.apache.coyote.http11;

import java.util.Map;

public class Cookie {

    private Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    public void removeCookie(String key) {
        cookies.remove(key);
    }

    public boolean hasCookie(String key) {
        return cookies.containsKey(key);
    }

}
