package nextstep.jwp.http;

import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public String jSessionId() {
        return cookie.computeIfAbsent(JSESSIONID, key -> UUID.randomUUID().toString());
    }
}
