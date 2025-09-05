package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private final Map<String, String> params;

    public QueryParameter() {
        this.params = new HashMap<>();
    }

    public QueryParameter(Map<String, String> params) {
        this.params = params;
    }

    public QueryParameter(QueryParameter queryParameter) {
        this.params = new HashMap<>(queryParameter.params);
    }

    public String getValue(String name) {
        try {
            return params.get(name);
        } catch (Exception e) {
            return null;
        }
    }
}
