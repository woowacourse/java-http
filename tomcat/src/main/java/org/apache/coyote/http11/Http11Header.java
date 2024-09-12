package org.apache.coyote.http11;

public record Http11Header(String key, String value) {

    public static final String SET_COOKIE = "Set-Cookie:";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String LOCATION = "Location";

    @Override
    public String toString() {
        return "%s:%s".formatted(key, value);
    }
}
