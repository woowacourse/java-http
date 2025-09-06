package org.apache.coyote.http.request;

import java.util.Collections;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> value;

    public RequestBody(Map<String, String> value) {
        this.value = Collections.unmodifiableMap(value);
    }

    public String getValue(String key) {
        return value.get(key);
    }
}
