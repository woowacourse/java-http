package org.apache.coyote.http11;

record HttpCookie(String name, String value) {

    static final String SESSION_COOKIE_KEY = "JSESSIONID";

    String toHeaderValue() {
        return name + "=" + value;
    }
}
