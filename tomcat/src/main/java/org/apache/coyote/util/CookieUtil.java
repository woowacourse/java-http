package org.apache.coyote.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;

public class CookieUtil {

    private static String VALUES_DELIMITER = ";";
    private static String COOKIE_DELIMITER = "=";

    private CookieUtil() {
    }

    public static Cookie read(List<String> values) {
        Map<String, String> cookies = Arrays.stream(values.getFirst().split(VALUES_DELIMITER))
                .map(value -> value.split(COOKIE_DELIMITER))
                .filter(element -> element.length == 2)
                .collect(Collectors.toMap(
                        element -> element[0].trim(),
                        element -> element[1].trim()
                ));
        return new Cookie(cookies);
    }
}
