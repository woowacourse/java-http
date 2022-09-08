package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public class RequestCookie {

    public static final String COOKIE_DELIMITER = "; ";
    public static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private final Map<String, String> cookies;

    public RequestCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static RequestCookie from(final String input) {
        Map<String, String> cookies = new HashMap<>();
        String[] splitInput = input.split(COOKIE_DELIMITER);
        for (String splitCookie : splitInput) {
            String[] cookie = splitCookie.split(COOKIE_KEY_VALUE_DELIMITER);
            cookies.put(cookie[KEY_INDEX], cookie[VALUE_INDEX]);
        }
        return new RequestCookie(cookies);
    }

    public String getJSessionValue() {
        return cookies.get("JSESSIONID");
    }
}
