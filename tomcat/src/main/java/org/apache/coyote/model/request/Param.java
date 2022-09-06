package org.apache.coyote.model.request;

import java.util.Map;

public class Param {

    private final Map<String, String> params;

    protected Param(final Map<String, String> params) {
        this.params = params;
    }

    public static Param of(final Map<String, String> params) {
        return new Param(params);
    }

    public String getByKey(String key) {
        return params.get(key);
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }
}
