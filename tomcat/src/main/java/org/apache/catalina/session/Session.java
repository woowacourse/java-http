package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public HttpCookie getCookie() {
        return new HttpCookie(JSESSIONID, id);
    }
}
