package nextstep.jwp.http;

import java.util.UUID;

public class HttpCookie {

    private final UUID value;

    public HttpCookie() {
        this.value = UUID.randomUUID();
    }

    public String getCookie() {
        return "JSESSIONID" + "=" + value.toString();
    }
}
