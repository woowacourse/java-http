package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntityHeader {

    protected static final String CONTENT_LENGTH = "Content-Length";
    protected static final String CONTENT_TYPE = "Content-Type";

    protected final Map<String, String> headers = new HashMap<>();

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(headers);
    }

}
