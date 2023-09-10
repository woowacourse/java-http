package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.BadRequestException;

public class HttpCookies {

    private static final String COOKIE_DELIMITER = ", ";
    private static final String ATTRIBUTE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int COOKIE_INDEX = 0;
    private static final int ATTRIBUTE_INDEX = 0;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, Cookie> cookies;

    private HttpCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookies from(String line) {
        Map<String, Cookie> cookies = new HashMap<>();

        String[] elements = line.split(COOKIE_DELIMITER);

        for (String element : elements) {
            String[] cookieAndAttribute = element.split(ATTRIBUTE_DELIMITER, 2);
            String[] cookieKeyAndValue = cookieAndAttribute[COOKIE_INDEX].split(KEY_VALUE_DELIMITER);

            Cookie cookie = Cookie.of(cookieKeyAndValue[COOKIE_VALUE_INDEX], cookieAndAttribute[ATTRIBUTE_INDEX]);

            cookies.put(cookieKeyAndValue[COOKIE_KEY_INDEX], cookie);
        }

        return new HttpCookies(cookies);
    }

    public static HttpCookies createEmptyCookies() {
        return new HttpCookies(new LinkedHashMap<>());
    }

    public void addCookie(String key, Cookie cookie) {
        cookies.put(key, cookie);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String getCookies() {
        return cookies.keySet()
                .stream()
                .map(key -> {
                    Cookie cookie = cookies.get(key);

                    if (cookie.hasAttribute()) {
                        return key + KEY_VALUE_DELIMITER + cookie.getValue() + ATTRIBUTE_DELIMITER
                                + cookie.getAttribute();
                    }

                    return key + KEY_VALUE_DELIMITER + cookie.getValue();
                })
                .collect(Collectors.joining(COOKIE_DELIMITER));
    }

    public Cookie get(String key) {
        validateKey(key);

        return cookies.get(key);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new BadRequestException("Cookie key is Null");
        }
    }

}
