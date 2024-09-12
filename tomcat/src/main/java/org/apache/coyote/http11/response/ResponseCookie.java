package org.apache.coyote.http11.response;

public class ResponseCookie {

    private static final String FORMAT_OF_COOKIE = "%s=%s";

    private final String key;
    private final String value;

    public ResponseCookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String buildHttpMessage() {
        return String.format(FORMAT_OF_COOKIE, key, value);
    }
}
