package nextstep.jwp.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cookies {

    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_KEY_VALUE = 1;

    private Map<String, String> cookies = new HashMap<>();

    public Cookies(String rawCookie) {
        if (!Objects.isNull(rawCookie)) {
            String[] splitCookies = rawCookie.split("; ");

            if (splitCookies.length != 0) {
                for (String cookie : splitCookies) {
                    String[] split = cookie.split("=");
                    String key = split[COOKIE_KEY_INDEX];
                    String value = split[COOKIE_KEY_VALUE];
                    cookies.put(key, value);
                }
            }
        }
    }

    public Map<String, String> getCookies() {
        return Collections.unmodifiableMap(cookies);
    }
}
