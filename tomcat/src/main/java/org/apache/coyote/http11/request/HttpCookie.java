package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final Map<String, String> VALUES = new HashMap<>();
    private static boolean DOES_NEED_TO_SET_COOKIE = false;

    public static HttpCookie of(String rawCookies) {
        String[] splitCookies = rawCookies.split("; ");
        for (String splitCookie : splitCookies) {
            String[] cookieKeyValue = splitCookie.split("=");
            VALUES.put(cookieKeyValue[0], cookieKeyValue[1]);
        }
        if (!VALUES.containsKey("JSESSIONID")) {
            String jSessionId = String.valueOf(UUID.randomUUID());
            VALUES.put("JSESSIONID", jSessionId);
            SessionManager.addSession(new Session(jSessionId));
            DOES_NEED_TO_SET_COOKIE = true;
            return new HttpCookie();
        }
        DOES_NEED_TO_SET_COOKIE = false;
        return new HttpCookie();
    }

    public static HttpCookie emptyCookie() {
        String jSessionId = String.valueOf(UUID.randomUUID());
        VALUES.put("JSESSIONID", jSessionId);
        SessionManager.addSession(new Session(jSessionId));
        DOES_NEED_TO_SET_COOKIE = true;
        return new HttpCookie();
    }

    public static boolean doesNeedToSetJSessionIdCookie() {
        return DOES_NEED_TO_SET_COOKIE;
    }

    public static void completeSetJSessionIdCookie() {
        DOES_NEED_TO_SET_COOKIE = false;
    }

    public boolean existsJSessionId() {
        return VALUES.containsKey("JSESSIONID");
    }

    public static String ofJSessionId() {
        return VALUES.get("JSESSIONID");
    }

    public String get(String name) {
        return VALUES.get(name);
    }
}
