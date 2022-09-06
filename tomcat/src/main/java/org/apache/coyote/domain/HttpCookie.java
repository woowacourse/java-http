package org.apache.coyote.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public boolean getJSESSIONID() {
        return httpCookie.containsKey("JSESSIONID");
    }

    public String getHeader() {
        UUID id = UUID.randomUUID();
        return "Set-Cookie: JSESSIONID=" + id;
    }
}
