package org.apache.coyote.http.cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http.HeaderKey;

public class Cookies {

    private static final String COOKIES_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final List<Cookie> cookies;

    private Cookies(List<Cookie> cookies) {
        this.cookies = new ArrayList<>(cookies);
    }

    public static Cookies from(Map<String, List<String>> header) {
        List<String> cookieValues = header.get(HeaderKey.COOKIE.value);
        if (cookieValues == null) {
            return Cookies.emptyCookies();
        }
        return new Cookies(decodeCookies(cookieValues.get(0)));
    }

    private static List<Cookie> decodeCookies(String cookiesString) {
        return Arrays.stream(cookiesString.split(COOKIES_DELIMITER))
                     .map(String::strip)
                     .map(cookieString -> cookiesString.split(KEY_VALUE_DELIMITER))
                     .filter(keyValue -> keyValue.length == 2)
                     .map(keyValue -> new Cookie(keyValue[0], keyValue[1]))
                     .collect(Collectors.toList());
    }

    public static Cookies emptyCookies() {
        return new Cookies(List.of());
    }

    public Optional<String> getCookie(String key) {
        return cookies.stream()
                      .filter(cookie -> cookie.hasKey(key))
                      .findFirst()
                      .map(Cookie::getValue);
    }
}
