package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> parsedHttpCookies;

    private HttpCookie(final Map<String, String> parsedHttpCookies) {
        this.parsedHttpCookies = parsedHttpCookies;
    }

    public static HttpCookie from(final String rawCookie) {
        final Map<String, String> parseHttpCookies = Arrays.stream(rawCookie.split(";\\s?"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(params -> params[0], params -> params[1]));

        return new HttpCookie(parseHttpCookies);
    }
}
