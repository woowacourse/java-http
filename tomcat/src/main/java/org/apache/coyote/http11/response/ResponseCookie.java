package org.apache.coyote.http11.response;

import org.apache.catalina.session.Session;

public class ResponseCookie {

    private static final String COOKIE_FORMAT = "%s=%s";

    private final String name;
    private final String value;

    public ResponseCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ResponseCookie of(Session session) {
        return new ResponseCookie("JSESSIONID", session.getId());
    }

    public String toResponse() {
        return String.format(COOKIE_FORMAT, name, value);
    }
}
