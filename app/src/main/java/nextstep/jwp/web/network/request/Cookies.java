package nextstep.jwp.web.network.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private static final Cookies EMPTY_COOKIES = new Cookies(new HashMap<>());
    private static final int GET_ALL_COOKIES = 0;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;
    private static final String DEFAULT_COOKIE_VALUE = null;

    private final Map<String, String> data;

    private Cookies(Map<String, String> data) {
        this.data = data;
    }

    public static Cookies of(String cookiesAsString) {
        if (cookiesAsString == null) {
            return EMPTY_COOKIES;
        }
        return new Cookies(
                Arrays.stream(allCookies(cookiesAsString))
                        .map(cookie -> cookie.split("="))
                        .collect(Collectors.toMap(
                                keyAndValue -> keyAndValue[COOKIE_KEY_INDEX].trim(),
                                keyAndValue -> keyAndValue[COOKIE_VALUE_INDEX].trim()))
        );
    }

    private static String[] allCookies(String cookiesAsString) {
        return cookiesAsString.split(";", GET_ALL_COOKIES);
    }

    public String get(String key) {
        return data.getOrDefault(key, DEFAULT_COOKIE_VALUE);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
