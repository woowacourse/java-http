package org.apache.coyote.http11;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CookieParser {

    public static List<Cookie> parse(final String cookieString) {
        final String[] tokens = cookieString.split("; ");
        return Stream.of(tokens)
                .map(Cookie::of)
                .collect(Collectors.toList());
    }
}
