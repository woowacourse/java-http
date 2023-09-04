package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final HashMap<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        final String[] params = cookieHeader.split("; ");
        final HashMap<String, String> cookies = initCookies(params);
        return new HttpCookie(cookies);
    }

    private static HashMap<String, String> initCookies(final String[] params) {
        final HashMap<String, String> data = new HashMap<>();
        for (final String param : params) {
            final String[] splitParam = param.split("=");
            final String paramKey = splitParam[0];
            final String paramValue = splitParam[1];
            data.put(paramKey, paramValue);
        }
        return data;
    }

    public boolean hasJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }
}
