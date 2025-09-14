package org.apache.coyote.http11.general;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieParser {

    public static Cookies parseFromHeaders(HttpHeaders headers) {
        String cookie = headers.getHeaderValueOf(CommonHeaderKeys.COOKIE.getKey());
        if (cookie == null) {
            return new Cookies(new HashMap<>());
        }
        String[] splittedCookie = cookie.split("; ");
        Map<String, String> cookies = Arrays.stream(splittedCookie)
            .collect(Collectors.toMap(
                (splitted -> splitted.split("=")[0]),
                (splitted -> splitted.split("=")[1]),
                (oldValue, newValue) -> newValue
            ));
        return new Cookies(cookies);
    }

}
