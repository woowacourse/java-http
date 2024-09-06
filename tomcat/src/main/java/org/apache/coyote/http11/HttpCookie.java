package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

//    public HttpCookie(String cookie) {
//        setCookies(cookie);
//    }
//
//    private void setCookies(String cookie) {
//        for (String cookiesPart : cookie.split(" ")) {
//            String[] keyValue = cookiesPart.split("=");
//            cookies.put(keyValue[0], keyValue[1]);
//        }
//    }

    public String getJsessionId() {
        return cookies.get(JSESSIONID);
    }
}
