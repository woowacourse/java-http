package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final int PARAM_INFO_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = new HashMap<>(cookies);
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
            final String paramInfo = splitParam[PARAM_INFO_INDEX];
            final String paramValue = splitParam[PARAM_VALUE_INDEX];
            data.put(paramInfo, paramValue);
        }
        return data;
    }

    public boolean hasJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSessionId() {
        if (hasJSessionId()) {
            return cookies.get("JSESSIONID");
        }
        return "";
    }
}
