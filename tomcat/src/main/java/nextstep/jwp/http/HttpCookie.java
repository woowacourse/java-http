package nextstep.jwp.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private final Map<String, String> values;

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie empty() {
        return new HttpCookie(new ConcurrentHashMap<>());
    }
}
