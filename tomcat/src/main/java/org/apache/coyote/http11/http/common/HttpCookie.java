package org.apache.coyote.http11.http.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.http.common.header.HttpHeader;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie() {
        this.values = new HashMap<>();
    }

    public static HttpCookie from(final HttpHeader httpHeader) {
        final HttpCookie httpCookie = new HttpCookie();
        final Optional<String> cookieOptional = httpHeader.getCookie();

        if (cookieOptional.isEmpty()) {
            return httpCookie;
        }

        final String rawCookie = cookieOptional.get();
        final String[] cookies = rawCookie.trim().split(";");

        for (final String cookie : cookies) {
            parseAndAddCookie(cookie, httpCookie);
        }

        return httpCookie;
    }

    private static void parseAndAddCookie(final String cookieElementLine, final HttpCookie httpCookie) {
        if (cookieElementLine == null || cookieElementLine.isBlank()) {
            return;
        }
        final String[] cookieElement = cookieElementLine.split("=", 2);

        if (cookieElement.length != 2) {
            return;
        }

        final String cookieName = cookieElement[0].trim();
        final String cookieValue = cookieElement[1].trim();

        if (cookieName.isEmpty()) {
            return;
        }
        httpCookie.addCookie(cookieName, cookieValue);
    }

    public void addCookie(final String name, final String value) {
        values.put(name, value);
    }

    public boolean containsName(final String name) {
        return values.containsKey(name);
    }

    public String getByName(final String name) {
        return values.get(name);
    }
}
