package org.apache.coyote.http11.http.common;

import http.HttpHeaderKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final HttpHeader httpHeader) {
        return httpHeader.getFirstValue(HttpHeaderKey.COOKIE.getValue())
                .map(HttpCookie::parseCookies)
                .map(HttpCookie::new)
                .orElseGet(() -> new HttpCookie(Collections.emptyMap()));
    }

    private static Map<String, String> parseCookies(final String rawCookie) {
        return Arrays.stream(rawCookie.trim().split(HttpSplitFormat.COOKIE.getValue()))
                .map(String::trim)
                .filter(rawCookieElement -> !rawCookieElement.isBlank())
                .map(rawCookieElement -> rawCookieElement.split(HttpSplitFormat.COOKIE_ELEMENT.getValue(), 2))
                .filter(cookieElement -> cookieElement.length == 2)
                .filter(cookieElement -> !cookieElement[0].trim().isEmpty())
                .collect(Collectors.toMap(
                        cookieElement -> cookieElement[0].trim(),
                        cookieElement -> cookieElement[1].trim()
                ));
    }

    public void addCookie(final String name, final String value) {
        values.put(name, value);
    }

    public String getByName(final String name) {
        return values.get(name);
    }
}
