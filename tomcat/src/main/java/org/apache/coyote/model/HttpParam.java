package org.apache.coyote.model;

import java.util.Map;

public class HttpParam {

    private final Map<String, String> params;

    protected HttpParam(final Map<String, String> params) {
        this.params = params;
    }

    public static HttpParam of(final Map<String, String> params) {
        return new HttpParam(params);
    }

    public String getByKey(String key) {
        return params.get(key);
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }
}
