package nextstep.jwp.web.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();
    private boolean isNew;

    public HttpSession(String id) {
        this.id = id;
        this.isNew = true;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void isNewToFalse() {
        this.isNew = false;
    }

    public boolean isNew() {
        return isNew;
    }

    public void invalidate() {
        values.clear();
    }
}
