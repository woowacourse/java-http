package nextstep.jwp.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpCookie {

    private static final String SESSION_KEY = "JSESSIONID";

    private final Map<String, String> storage;

    public HttpCookie() {
        this(new ConcurrentHashMap<>());
    }

    private HttpCookie(Map<String, String> storage) {
        this.storage = storage;
    }

    public void setStorage(String key, String value) {
        storage.put(key, value);
    }

    public boolean isJSessionId() {
        return storage.containsKey(SESSION_KEY);
    }

    public String getJSessionId() {
        return storage.get(SESSION_KEY);
    }

    public Map<String, String> getStorage() {
        return storage;
    }
}
