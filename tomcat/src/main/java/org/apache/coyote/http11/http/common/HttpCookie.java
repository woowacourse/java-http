package org.apache.coyote.http11.http.common;

import http.HttpHeaderKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
        Map<String, String> values = new HashMap<>();
        final String[] cookieElements = rawCookie.trim().split(HttpSplitFormat.COOKIE.getValue());

        for (String cookieElement : cookieElements) {
            if (cookieElement.isBlank()) {
                continue;
            }

            final String[] splits = cookieElement.split(HttpSplitFormat.COOKIE_ELEMENT.getValue(), 2);
            if (splits.length != 2) {
                continue;
            }

            final String cookieName = splits[0].trim();
            final String cookieValue = splits[1].trim();
            if (cookieName.isEmpty()) {
                continue;
            }

            values.put(cookieName, cookieValue);
        }
        return values;
    }


    public void addCookie(final String name, final String value) {
        values.put(name, value);
    }

    public String getByName(final String name) {
        return values.get(name);
    }
}
