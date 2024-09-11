package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    public static final String SET_COOKIE_PREFIX = "Set-Cookie: ";
    public static final String COOKIE_SEPARATOR = ";";
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String SESSION_ID_NAME = "JSESSIONID";
    public static final int COOKIE_KEY_INDEX = 0;
    public static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    public HttpCookie(String cookies) {
        this.cookies = new HashMap<>();
        initialize(cookies);
    }

    public static String appendSetCookieHeader(String response, String sessionId) {
        return String.join("\r\n",
                response + " ",
                SET_COOKIE_PREFIX + SESSION_ID_NAME + KEY_VALUE_SEPARATOR + sessionId + " "
        );
    }

    private void initialize(String cookieString) {
        if (cookieString.isEmpty()) {
            return;
        }
        String[] cookie = cookieString.split(COOKIE_SEPARATOR);
        for (String keyValue : cookie) {
            String[] split = keyValue.split(KEY_VALUE_SEPARATOR);
            cookies.put(split[COOKIE_KEY_INDEX].trim(), split[COOKIE_VALUE_INDEX]);
        }
    }

    public String getCookie(String cookieName) {
        String cookie = cookies.get(cookieName);
        if (cookie == null) {
            return "";
        }
        return cookie;
    }
}
