package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final int PARAM_KEY_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(String cookie) {
        String[] params = cookie.split("; ");
        HashMap<String, String> cookies = initCookies(params);
        return new Cookie(cookies);
    }

    private static HashMap<String, String> initCookies(String[] params) {
        HashMap<String, String> cookies = new HashMap<>();
        for (String param : params) {
            String[] splitParam = param.split("=");
            String key = splitParam[PARAM_KEY_INDEX];
            String value = splitParam[PARAM_VALUE_INDEX];
            cookies.put(key, value);
        }
        return cookies;
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
