package org.apache.coyote.domain;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.session.Session;

public class HttpCookie {

    private final Map<String, String> httpCookie;

    private HttpCookie(Map<String, String> httpCookie) {
        this.httpCookie = httpCookie;
    }

    public static HttpCookie from(String cookie) {
        Map<String, String> httpCookie = new HashMap<>();
        if (cookie.length() == 0) {
            return new HttpCookie(httpCookie);
        }
        String[] cookies = cookie.split("; ");
        for (String cookieUnit : cookies) {
            String[] keyAndValue = cookieUnit.split("=");
            httpCookie.put(keyAndValue[0], keyAndValue[1]);
        }
        return new HttpCookie(httpCookie);
    }

    public boolean hasJSESSIONID() {
        return httpCookie.containsKey("JSESSIONID");
    }

    public String getJSESSIONID() {
        return httpCookie.get("JSESSIONID");
    }

    public String getHeader() {
        return "Set-Cookie: JSESSIONID=" + this.getJSESSIONID();
    }

    public void add(Session session) {
        httpCookie.put("JSESSIONID", session.getId());
    }
}
