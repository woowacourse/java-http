package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private final Map<String, Object> fields = new HashMap<>();

    public void put(String name, Object value) {
        fields.put(name, value);
    }

    public Object get(String name) {
        return fields.get(name);
    }

    public void setContentType(ContentType contentType) {
        fields.put("Content-Type", contentType.getMediaType());
    }

    public long getContentLength() {
        return (long) fields.getOrDefault("Content-Length", 0L);
    }

    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}
