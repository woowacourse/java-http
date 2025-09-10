package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Optional;
import org.apache.coyote.http11.request.dto.HttpRequest;

public final class CookieUtils {

    private static final String COOKIE_DELIMITER = "=";

    private CookieUtils() {}

    public static Optional<String> getCookie(HttpRequest request, String name) {
        return request.headers()
                .getFirst("Cookie")
                .flatMap(cookieHeader -> Arrays.stream(cookieHeader.split(";"))
                        .map(String::strip)
                        .map(c -> c.split(COOKIE_DELIMITER, 2))
                        .filter(parts -> hasKey(name, parts))
                        .map(parts -> parts[1].strip())
                        .findFirst());
    }

    private static boolean hasKey(String name, String[] parts) {
        return parts.length == 2 && parts[0].strip().equals(name);
    }
}
