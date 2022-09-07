package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private final Map<String, String> values;
    private final boolean doesNeedToJSessionCookie;

    public HttpCookie(Map<String, String> values, boolean doesNeedToJSessionCookie) {
        this.values = values;
        this.doesNeedToJSessionCookie = doesNeedToJSessionCookie;
    }

    public static HttpCookie of(String rawCookies) {
        Map<String, String> cookies = new LinkedHashMap<>();
        String[] splitCookies = rawCookies.split("; ");
        for (String splitCookie : splitCookies) {
            String[] cookieKeyValue = splitCookie.split("=");
            cookies.put(cookieKeyValue[0], cookieKeyValue[1]);
        }
        if (cookies.containsKey("JSESSIONID")) {
            return new HttpCookie(cookies, false);
        }
        return setJSessionIdCookie(cookies);
    }

    public static HttpCookie emptyCookie() {
        Map<String, String> cookies = new LinkedHashMap<>();
        return setJSessionIdCookie(cookies);
    }

    private static HttpCookie setJSessionIdCookie(Map<String, String> cookies) {
        String jSessionId = String.valueOf(UUID.randomUUID());
        cookies.put("JSESSIONID", jSessionId);
        SessionManager.addSession(new Session(jSessionId));
        return new HttpCookie(cookies, true);
    }

    public boolean doesNeedToSetJSessionIdCookie() {
        return doesNeedToJSessionCookie;
    }

    public String get(String name) {
        return values.get(name);
    }
}
