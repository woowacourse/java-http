package org.apache.coyote.domain.response;

public class RedirectUrl implements Header{

    private final String value;

    private RedirectUrl(String value) {
        this.value = value;
    }

    public static RedirectUrl from(String redirectUrl) {
        return new RedirectUrl(redirectUrl);
    }

    @Override
    public String getHeader() {
        return "Location: " + value + "\r\n";
    }
}
