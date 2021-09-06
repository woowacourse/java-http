package nextstep.jwp.framework.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private static final long DEFAULT_EXPIRATION_PERIOD = 1000 * 60 * 30;

    private final String id;
    private long accessTime = System.currentTimeMillis();
    private final long expirationPeriod;
    private final Map<String, Object> params = new ConcurrentHashMap<>();


    public HttpSession(String id, long expirationPeriod) {
        this.id = id;
        this.expirationPeriod = expirationPeriod;
    }

    public HttpSession(String id) {
        this(id, DEFAULT_EXPIRATION_PERIOD);
    }

    public void put(String name, Object value) {
        params.put(name, value);
    }

    public Optional<Object> take(String name) {
        return Optional.ofNullable(params.get(name));
    }

    public void remove(String name) {
        params.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(id);
    }

    public void refreshAccessTime() {
        this.accessTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime - accessTime > expirationPeriod;
    }

    public String getId() {
        return id;
    }

    public long getAccessTime() {
        return accessTime;
    }
}
