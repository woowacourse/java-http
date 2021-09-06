package nextstep.jwp.framework.session;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private static final HttpSession INVALID_SESSION = new HttpSession("", -1, Collections.emptyMap());
    private static final long DEFAULT_EXPIRATION_PERIOD = 10000;

    private final String id;
    private long accessTime = System.currentTimeMillis();
    private final long expirationPeriod;
    private final Map<String, Object> params;

    private HttpSession(String id, long expirationPeriod, Map<String, Object> params) {
        this.id = id;
        this.expirationPeriod = expirationPeriod;
        this.params = params;
    }

    public HttpSession(String id, long expirationPeriod) {
        this(id, expirationPeriod, new ConcurrentHashMap<>());
    }

    public HttpSession(String id) {
        this(id, DEFAULT_EXPIRATION_PERIOD);
    }

    public static HttpSession invalid() {
        return INVALID_SESSION;
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

    public boolean isValid() {
        return !isInvalid();
    }

    public boolean isInvalid() {
        return this == INVALID_SESSION;
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime - accessTime > expirationPeriod;
    }

    public boolean contains(String name) {
        return params.containsKey(name);
    }

    public String getId() {
        return id;
    }

    public long getAccessTime() {
        return accessTime;
    }
}
