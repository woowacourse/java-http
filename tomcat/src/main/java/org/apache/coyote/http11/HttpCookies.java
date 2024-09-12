package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class HttpCookies {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE_REGEX = "; ";
    private static final String KEY_VALUE_REGEX = "=";
    private static final int KEY_VALUE_LENGTH = 2;

    private List<HttpCookie> cookies;

    public HttpCookies(String cookies) {
        this(parseCookies(cookies));
    }

    public HttpCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public static List<HttpCookie> parseCookies(String cookies) {
        List<HttpCookie> result = new ArrayList<>();

        String[] parts = cookies.split(COOKIE_REGEX);
        for (String part : parts) {
            String[] keyValue = part.trim().split(KEY_VALUE_REGEX);
            if (keyValue.length == KEY_VALUE_LENGTH) {
                result.add(new HttpCookie(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]));
            }
        }
        return result;
    }

    public HttpCookie getCookie(String name) {
        return cookies.stream()
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .orElse(new HttpCookie(name, ""));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (HttpCookie cookie : cookies) {
            if (!sb.isEmpty()) {
                sb.append(COOKIE_REGEX);
            }
            sb.append(cookie.toString());
        }
        return sb.toString();
    }
}
