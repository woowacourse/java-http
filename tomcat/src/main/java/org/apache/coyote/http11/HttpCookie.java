package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_UNIT_DELIMITER = ";";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookies) {
        return new HttpCookie(getCookie(cookies));
    }

    public static HttpCookie getEmptyValue() {
        return new HttpCookie(Map.of());
    }

    private static Map<String, String> getCookie(String cookies) {
        String[] dividedCookies = cookies.split(COOKIE_UNIT_DELIMITER);
        return Arrays.stream(dividedCookies)
                .map(cookie -> cookie.split(COOKIE_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public boolean isExistJSESSIONID() {
        return cookies.containsKey("JSESSIONID");
    }
}
