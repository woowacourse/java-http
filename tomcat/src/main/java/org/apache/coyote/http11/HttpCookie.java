package org.apache.coyote.http11;

import org.apache.coyote.http11.util.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {
    private static final String COOKIE_DELIMITER = "=";
    private static final String JSESSIONID = "JSESSIONID";
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie parseCookie(final String cookie) {
        Map<String,String> parsedCookie = new HashMap<>();
        if(cookie!=null) {
            parsedCookie = Parser.cookieParse(cookie);
        }

        return new HttpCookie(parsedCookie);
    }

    public String makeCookieValue(final UUID uuid){
        return JSESSIONID + COOKIE_DELIMITER + uuid;
    }

    public boolean checkSessionId(){
        return cookies.containsKey(JSESSIONID);
    }

    public String getSessionId(){
        return cookies.get(JSESSIONID);
    }
}
