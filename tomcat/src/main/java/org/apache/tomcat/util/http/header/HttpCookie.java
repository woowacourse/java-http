package org.apache.tomcat.util.http.header;

import static org.apache.catalina.session.Session.SESSION_ID_IDENTIFICATION;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.session.Session;

public class HttpCookie {

    public static final String DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
    }

    public static String getCookieString(Session session) {
        return SESSION_ID_IDENTIFICATION + DELIMITER + session.getSessionId();
    }

    public String get(String cookieName) {
        return cookies.get(cookieName);
    }
}
