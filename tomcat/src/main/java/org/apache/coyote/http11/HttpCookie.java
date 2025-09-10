package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {

    private static final String SET_COOKIE_HEADER_FORMAT = "Set-Cookie: %s\r\n";
    private static final String COOKIE_FORMAT = "%s=%s";
    private static final String COOKIE_SEPARATOR = "; ";

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void setCookie(String name, String value) {
        cookies.put(name, value);
    }

    public String getCookie(String name) {
        return cookies.getOrDefault(name, "");
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String getSetCookie() {
        List<String> keyValues = new ArrayList<>();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            String keyValue = String.format(COOKIE_FORMAT, entry.getKey(), entry.getValue());
            keyValues.add(keyValue);
        }
        return String.format(SET_COOKIE_HEADER_FORMAT,String.join(COOKIE_SEPARATOR, keyValues));
    }
}
