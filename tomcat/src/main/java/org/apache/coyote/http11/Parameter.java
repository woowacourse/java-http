package org.apache.coyote.http11;

import java.util.Map;

public class Parameter {

    private final Map<String, String> params;

    public Parameter(Map<String, String> params) {
        this.params = params;
    }

    public String getValue(String name) {
        return params.getOrDefault(name, "");
    }
}
