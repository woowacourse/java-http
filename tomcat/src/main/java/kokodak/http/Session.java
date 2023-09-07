package kokodak.http;

import java.util.HashMap;
import java.util.Map;
import kokodak.handler.Argument;

public class Session implements Argument {

    private String id;
    private Map<String, Object> values;

    public Session(final String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }

    public String getId() {
        return id;
    }

    @Override
    public Class<? extends Argument> getImlClass() {
        return getClass();
    }
}
