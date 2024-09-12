package org.apache.tomcat.util.http.header;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    public static final String SESSION_ID_IDENTIFICATION = "JSESSIONID";
    public static final String DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public String get(String cookieName) {
        return cookies.get(cookieName);
    }
}
