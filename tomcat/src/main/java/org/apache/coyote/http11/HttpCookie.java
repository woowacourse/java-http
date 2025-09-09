package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        Arrays.stream(cookieHeader.split(";"))
            .map(cookie -> cookie.split("="))
            .forEach(keyValue -> cookies.put(keyValue[0].trim(), keyValue[1].trim()));
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
