package org.apache.coyote.http11.cookie;

public enum SetCookieOption {

    MAX_AGE("Max-Age"),
    DOMAIN("Domain"),
    PATH("Path"),
    SECURE("Secure"),
    HTTP_ONLY("HttpOnly");

    private final String optionKey;

    SetCookieOption(final String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionKey() {
        return optionKey;
    }
}
