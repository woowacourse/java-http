package org.apache.coyote.http11;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookieParser {

    private static final String COOKIE_DELIMITER = "; ";

    public static List<Cookie> parse(final String cookieString) {
        final String[] tokens = cookieString.split(COOKIE_DELIMITER);
        return Stream.of(tokens)
                .map(Cookie::of)
                .collect(Collectors.toList());
    }
}
