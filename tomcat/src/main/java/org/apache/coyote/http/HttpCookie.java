package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final int PARAM_INFO_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;
    private static final String SESSION_ID_KEY = "JSESSIONID";
    private static final String COOKIES_DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        final String[] params = cookieHeader.split(COOKIES_DELIMITER);
        final HashMap<String, String> cookies = initCookies(params);
        return new HttpCookie(cookies);
    }

    private static HashMap<String, String> initCookies(final String[] params) {
        final HashMap<String, String> values = new HashMap<>();
        for (final String param : params) {
            final String[] splitParam = param.split(COOKIE_DELIMITER);
            final String paramInfo = splitParam[PARAM_INFO_INDEX];
            final String paramValue = splitParam[PARAM_VALUE_INDEX];
            values.put(paramInfo, paramValue);
        }
        return values;
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(SESSION_ID_KEY);
    }

    public String getJSessionId() {
        if (hasJSessionId()) {
            return cookies.get(SESSION_ID_KEY);
        }
        return "";
    }
}
