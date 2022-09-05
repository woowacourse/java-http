package org.apache.coyote.domain;

public class RedirectUrl {

    private final String redirectUrl;

    private RedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static RedirectUrl from(String redirectUrl) {
        return new RedirectUrl(redirectUrl);
    }

    public String getHeader() {
        return "Location: " + redirectUrl;
    }
}
