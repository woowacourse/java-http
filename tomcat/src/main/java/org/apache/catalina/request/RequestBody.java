package org.apache.catalina.request;

import java.util.HashMap;
import java.util.Map;

public record RequestBody(Map<String, String> body) {

    @Override
    public Map<String, String> body() {
        return new HashMap<>(body);
    }
}
