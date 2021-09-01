package nextstep.jwp.framework.http.request.util;

import java.util.HashMap;
import java.util.Map;

public class CookieValueExtractor {

    private static final String COOKIE_VALUE_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private CookieValueExtractor(){
    }

    public static Map<String, String> extract(final String cookieHeader) {
        final Map<String, String> cookieValues = new HashMap<>();
        for (String cookie : cookieHeader.split(COOKIE_VALUE_SEPARATOR)) {
            final String[] cookieValue = cookie.split(KEY_VALUE_SEPARATOR, 2);
            cookieValues.put(cookieValue[0].trim(), cookieValue[1].trim());
        }
        return cookieValues;
    }
}
