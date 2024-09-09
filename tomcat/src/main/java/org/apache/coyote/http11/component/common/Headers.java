package org.apache.coyote.http11.component.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Headers {
    protected static final String PARAMETER_DELIMITER = "\r\n";
    protected static final String KEY_VALUE_DELIMITER = ":";

    protected final Map<String, String> values = new ConcurrentHashMap<>();

    protected String getValue(final String key) {
        return values.get(key);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
