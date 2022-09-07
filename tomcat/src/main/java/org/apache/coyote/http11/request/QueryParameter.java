package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.support.ParameterBinder;

public class QueryParameter {

    private final Map<String, String> params;

    private QueryParameter(Map<String, String> params) {
        this.params = params;
    }

    public static QueryParameter from(String query) {
        return new QueryParameter(ParameterBinder.bind(query));
    }

    public static QueryParameter empty() {
        return new QueryParameter(new HashMap<>());
    }

    public Map<String, String> getParams() {
        return params;
    }
}
