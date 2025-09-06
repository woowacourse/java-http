package org.apache.catalina.servlet.cookie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.servletContainer.session.Session;
import org.apache.catalina.servletContainer.session.SessionManager;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    public static final String COOKIES_SEPARATOR = ";";
    public static final String COOKIE_SEPARATOR = "=";

    private final Map<String, String> cookies;

    public HttpCookie(final String cookies) {
        this.cookies = initCookies(cookies);
    }

    private Map<String, String> initCookies(final String cookies) {
        List<String> httpCookies = List.of(cookies.split(COOKIES_SEPARATOR));

        return httpCookies.stream()
                .map(cookie -> cookie.split(COOKIE_SEPARATOR))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }

    public static HttpCookie loginCookie(String sessionId) {
        return new HttpCookie(JSESSIONID + COOKIE_SEPARATOR + sessionId);
    }

    public Session getSession() {
        String sessionId = cookies.get(JSESSIONID);
        return SessionManager.getInstance().findSession(sessionId);
    }

    public String combine() {
        return cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + COOKIE_SEPARATOR + entry.getValue())
                .collect(Collectors.joining(COOKIES_SEPARATOR));
    }
}
