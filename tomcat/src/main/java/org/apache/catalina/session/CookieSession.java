package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.cookie.HttpCookie;

public class CookieSession implements Session {

    private static final String COOKIE_SESSION_KEY = "JSESSIONID";

    private String id;
    private final Map<String, Object> values;


    public CookieSession(final String id, final Map<String, Object> values) {
        this.id = id;
        this.values = new HashMap<>(values);
    }

    public static CookieSession fromValues(final Map<String, Object> values) {
        return new CookieSession(UUID.randomUUID().toString(), values);
    }

    public HttpCookie createCookie() {
        return HttpCookie.of(COOKIE_SESSION_KEY, id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Object getAttribute(final String name) {
        return this.values.get(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        this.values.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        this.values.remove(name);
    }

    @Override
    public void invalidate() {
        this.values.clear();
    }
}
