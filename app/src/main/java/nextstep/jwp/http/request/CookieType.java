package nextstep.jwp.http.request;

public enum CookieType {
    JSESSIONID("JSESSIONID");

    private final String cookieKey;

    CookieType(String cookieKey) {
        this.cookieKey = cookieKey;
    }

    public String value() {
        return cookieKey;
    }
}
