package org.apache.coyote.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String ELEMENT_DELIMITER = "=";

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return new HttpCookie(new LinkedHashMap<>());
        }
        return new HttpCookie(
                Arrays.stream(cookies.split(COOKIE_DELIMITER))
                        .map(cookie -> cookie.split(ELEMENT_DELIMITER))
                        .filter(cookie -> cookie.length == 2)
                        .collect(Collectors.toMap(
                                cookie -> cookie[0],
                                cookie -> cookie[1],
                                (exist, replace) -> replace,
                                LinkedHashMap::new
                        ))
        );
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }
}
