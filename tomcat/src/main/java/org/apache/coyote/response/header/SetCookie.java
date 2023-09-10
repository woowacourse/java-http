package org.apache.coyote.response.header;

public class SetCookie {
    private final String cookie;

    public SetCookie(final String cookie) {
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "Set-Cookie: " + cookie;
    }
}
