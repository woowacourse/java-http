package org.apache.coyote.domain;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.domain.response.Header;
import org.apache.coyote.session.Session;

public class HttpCookie implements Header {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_REGEX = "; ";
    private static final String COOKIE_DELIMITER = "=";
    private static final int COOKIE_KEY = 0;
    private static final int COOKIE_VALUE = 1;
    private static final int NOT_EXIST_COOKIE = 0;

    private final Map<String, String> httpCookie;

    private HttpCookie(Map<String, String> httpCookie) {
        this.httpCookie = httpCookie;
    }

    public static HttpCookie from(String cookie) {
        Map<String, String> httpCookie = new HashMap<>();
        if (cookie.length() == NOT_EXIST_COOKIE) {
            return new HttpCookie(httpCookie);
        }
        String[] cookies = cookie.split(COOKIE_REGEX);
        for (String cookieUnit : cookies) {
            String[] keyAndValue = cookieUnit.split(COOKIE_DELIMITER);
            httpCookie.put(keyAndValue[COOKIE_KEY], keyAndValue[COOKIE_VALUE]);
        }
        return new HttpCookie(httpCookie);
    }

    public boolean hasJSESSIONID() {
        return httpCookie.containsKey(JSESSIONID);
    }

    public String getJSESSIONID() {
        return httpCookie.get(JSESSIONID);
    }

    public void add(Session session) {
        httpCookie.put(JSESSIONID, session.getId());
    }

    @Override
    public String getHeader() {
        return "Set-Cookie: " + JSESSIONID + " " + this.getJSESSIONID();
    }
}
