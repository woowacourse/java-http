package org.apache.tomcat.http.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Headers {

    public static final String CONTENT_LENGTH = "Content-Length";

    protected static final String PARAMETER_DELIMITER = "\r\n";
    protected static final String KEY_VALUE_DELIMITER = ":";

    protected final Map<String, String> values = new ConcurrentHashMap<>();

    protected String getValue(final String key) {
        return values.get(key);
    }
}
