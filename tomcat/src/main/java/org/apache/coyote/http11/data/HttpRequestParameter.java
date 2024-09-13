package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestParameter {
    private final Map<String, String> parameters;

    public HttpRequestParameter(Map<String, String> parameters) {
        this.parameters = new HashMap<>(parameters);
    }

    public String getValue(String key) {
        return parameters.get(key);
    }
}
