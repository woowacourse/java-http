package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Cookies {

    public static final Cookies EMPTY_COOKIES = new Cookies(new HashMap<>());
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private Cookies(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookies from(final String request) {
        final Map<String, String> cookies = Arrays.stream(request.split(COOKIE_SEPARATOR))
                                                  .map(value -> value.split(KEY_VALUE_SEPARATOR))
                                                  .collect(toMap(value -> value[KEY_INDEX], value -> value[VALUE_INDEX]));

        return new Cookies(cookies);
    }

    public String getValueBy(final String key) {
        return cookies.get(key);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
