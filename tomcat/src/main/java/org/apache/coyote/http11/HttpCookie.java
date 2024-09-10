package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private Map<String, String> cookies = new HashMap<>();

    public static HttpCookie ofJSessionId(String sessionId) {
        HttpCookie cookie = new HttpCookie();
        cookie.cookies.put(JSESSIONID, sessionId);
        return cookie;
    }

    private HttpCookie() {
    }

    public String getJsessionId() {
        return cookies.get(JSESSIONID);
    }
}
