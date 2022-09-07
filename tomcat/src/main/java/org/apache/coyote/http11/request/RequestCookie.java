package org.apache.coyote.http11.request;

import java.util.NoSuchElementException;

public class RequestCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Params cookies;

    public RequestCookie(final Params cookies) {
        this.cookies = cookies;
    }

    public static RequestCookie parse(final RequestHeader header) {
        return new RequestCookie(parseCookie(header));
    }

    private static Params parseCookie(final RequestHeader header) {
        return header.find("Cookie")
                .map(Params::parse)
                .orElse(Params.empty());
    }

    public String getSessionId() {
        try {
            return cookies.find(JSESSIONID);
        } catch (final NoSuchElementException e) {
            return null;
        }
    }
}
