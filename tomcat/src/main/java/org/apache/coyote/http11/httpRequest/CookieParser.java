package org.apache.coyote.http11.httpRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.general.Cookies;

public class CookieParser {

    public static final String HEADER_KEY_OF_COOKIE = "Cookie";

    public static Cookies parseFromHttpRequest(HttpRequest httpRequest) {
        String cookie = httpRequest.getHeaderValueOf(HEADER_KEY_OF_COOKIE);
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
