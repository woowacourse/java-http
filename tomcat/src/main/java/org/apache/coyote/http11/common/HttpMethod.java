package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET, POST, PUT, DELETE,
    HEAD, OPTIONS, TRACE, CONNECT, PATCH;

    private static final Map<String, HttpMethod> CACHE = new HashMap<>();

    static {
        for (HttpMethod method : values()) {
            CACHE.put(method.name(), method);
        }
    }

    public static HttpMethod from(String methodName) {
        if (!CACHE.containsKey(methodName)) {
            throw new IllegalArgumentException("Unknown Http Method: " + methodName);
        }
        return CACHE.get(methodName);
    }
}
