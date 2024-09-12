package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private Map<String, String> cookies = new HashMap<>();

    private HttpCookie() {
    }

    public HttpCookie(String cookie) {
        if (cookie != null) {
            for (String cookieParts : cookie.split(" ")) {
                String[] keyAndValue = cookieParts.split("=");
                cookies.put(keyAndValue[0], keyAndValue[1]);
            }
        }
    }

    public static HttpCookie ofJSessionId(String sessionId) {
        HttpCookie cookie = new HttpCookie();
        cookie.cookies.put(JSESSIONID, sessionId);
        return cookie;
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
