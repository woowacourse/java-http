package org.apache.coyote.http11;

public record HttpCookie(String name, String value) {

    static final String SESSION_COOKIE_KEY = "JSESSIONID";

    public String toHeaderValue() {
        return name + "=" + value;
    }
}
