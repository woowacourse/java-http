package org.apache.coyote.http11;

import java.util.Map;

public class Cookies {

    private Map<String, String> cookies;

    public Cookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }

}
