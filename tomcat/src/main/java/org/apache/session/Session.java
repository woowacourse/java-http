package org.apache.session;

import java.util.Map;

public class Session {
    private final String id;

    private final Map<String, Object> values;

    public Session(String id, Map<String, Object> values) {
        this.id = id;
        this.values = values;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Object getAttribute(String id){
        return values.get(id);
    }
}
