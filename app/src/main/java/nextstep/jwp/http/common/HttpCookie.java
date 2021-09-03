package nextstep.jwp.http.common;

import nextstep.jwp.exception.NotfoundCookieException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_AND_VALUE_DELIMITER = "=";
    private static final String PREFIX_COOKIE_RESPONSE = "Set-Cookie:";

    private final Map<String, String> values;

    public HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String cookies) {
        return new HttpCookie(parseCookie(cookies));
    }

    private static Map<String, String> parseCookie(String cookies) {
        Map<String, String> cookieValues = new HashMap<>();
        for (String cookie : cookies.split(COOKIE_DELIMITER)) {
            String[] cookieKeyAndValue = cookie.split(COOKIE_KEY_AND_VALUE_DELIMITER);
            cookieValues.put(cookieKeyAndValue[0].trim(), cookieKeyAndValue[1].trim());
        }
        return cookieValues;
    }

    public boolean hasKey(String key) {
        return values.containsKey(key);
    }

    public String getCookie(String cookieKey) {
        if (!values.containsKey(cookieKey)) {
            throw new NotfoundCookieException(cookieKey);
        }

        return values.get(cookieKey);
    }

    @Override
    public String toString() {
        String response = values.entrySet().stream()
                .map(entrySet -> entrySet.getKey() + "=" + entrySet.getValue())
                .collect(Collectors.joining("; "));
        return PREFIX_COOKIE_RESPONSE + response;
    }
}
