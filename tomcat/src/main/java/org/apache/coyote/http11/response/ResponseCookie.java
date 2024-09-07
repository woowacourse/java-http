package org.apache.coyote.http11.response;

import jakarta.servlet.http.HttpSession;

public class ResponseCookie {

    private static final String COOKIE_FORMAT = "%s=%s";

    private final String name;
    private final String value;

    public ResponseCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ResponseCookie of(HttpSession session) {
        return new ResponseCookie("JSESSIONID", session.getId());
    }

    public String toResponse() {
        return String.format(COOKIE_FORMAT, name, value);
    }
}
