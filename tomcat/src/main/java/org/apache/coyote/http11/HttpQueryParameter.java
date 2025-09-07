package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpQueryParameter {

    private final Map<String, String> params;

    public HttpQueryParameter(Map<String, String> params) {
        this.params = params;
    }

    public String getValue(String name) {
        try {
            return params.get(name);
        } catch (Exception e) {
            return null;
        }
    }
}
