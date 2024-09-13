package org.apache.tomcat.http.common;

import java.util.HashMap;
import java.util.Map;

public abstract class Headers {

    public static final String CONTENT_LENGTH = "Content-Length";

    protected static final String PARAMETER_DELIMITER = "\r\n";
    protected static final String KEY_VALUE_DELIMITER = ":";

    protected final Map<String, String> values = new HashMap<>();

    protected String getValue(final String key) {
        return values.get(key);
    }
}
