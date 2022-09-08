package org.apache.coyote.domain.response;

public class RedirectUrl implements Header{

    private final String redirectUrl;

    private RedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static RedirectUrl from(String redirectUrl) {
        return new RedirectUrl(redirectUrl);
    }

    @Override
    public String getHeader() {
        return "Location: " + redirectUrl + "\r\n";
    }
}
