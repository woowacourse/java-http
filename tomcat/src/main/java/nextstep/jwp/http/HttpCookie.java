package nextstep.jwp.http;

import java.util.UUID;

public class HttpCookie {

    private static final String COOKIE_NAME = "JSESSIONID";
    private final UUID value;

    public HttpCookie() {
        this.value = UUID.randomUUID();
    }

    public String getCookie() {
        return COOKIE_NAME + "=" + value.toString();
    }
}
