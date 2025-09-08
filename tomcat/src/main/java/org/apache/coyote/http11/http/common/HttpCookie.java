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
        Optional<String> cookieOptional = httpHeader.getCookie();
        if (cookieOptional.isEmpty()) {
            return httpCookie;
        }
        String rawCookie = cookieOptional.get();
        String[] cookies = rawCookie.trim().split(";");
        for (String cookie : cookies) {
            String[] cookieElement = cookie.split("=");
            String cookieName = cookieElement[0].trim();
            String cookieValue = cookieElement[1].trim();
            httpCookie.addCookie(cookieName, cookieValue);
        }
        return httpCookie;
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
