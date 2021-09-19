package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    Map<String, String> cookies = new HashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(String requestCookie) {
        String[] cookie = requestCookie.split("; ");
        for (String c : cookie) {
            final String[] split = c.split("=");
            this.cookies.put(split[0], split[1]);
        }
    }

    public String getCookies(String key) {
        return cookies.get(key);
    }
}
