package nextstep.jwp.http.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestCookie {

    private final Map<String, String> cookies;

    public RequestCookie(Map<String, String> cookies) {
        this.cookies = new ConcurrentHashMap<>(cookies);
    }

    public boolean containsKey(String key) {
        return cookies.containsKey(key);
    }

    public String get(String key) {
        return cookies.get(key);
    }
}
