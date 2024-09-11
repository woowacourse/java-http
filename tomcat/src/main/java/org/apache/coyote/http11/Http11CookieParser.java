package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpCookie;
import org.apache.coyote.HttpCookies;

public class Http11CookieParser {

    public static HttpCookies parse(String cookie) {
        Map<String, HttpCookie> cookies = new HashMap<>();
        String[] cookiePairs = cookie.split(";");
        for (String cookiePair : cookiePairs) {
            int index = cookiePair.indexOf("=");
            if (index == -1) {
                break;
            }
            String name = cookiePair.substring(0, index).trim();
            String value = cookiePair.substring(index + 1).trim();
            HttpCookie httpCookie = new HttpCookie(name, value);
            cookies.put(name, httpCookie);
        }
        return new HttpCookies(cookies);
    }
}
