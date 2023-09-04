package nextstep.jwp;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private static final String SEPARATOR = "; ";
    private static final String DELIMITER = "=";

    private final Map<String, String> cookies = new ConcurrentHashMap<>();

    public HttpCookie() {
    }


    public void add(String key, String value) {
        cookies.put(key, value);
    }

    public void add(String key, UUID value) {
        cookies.put(key, UUID.randomUUID().toString());
    }

    public String get(String key) {
        return cookies.get(key);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
}
