package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private final Map<String, String> params;

    public QueryParameters(Map<String, String> params) {
        this.params = params;
    }

    public QueryParameters(String queryString) {
        params = new HashMap<>();
        if(queryString.isEmpty()) {
            return;
        }
        String[] params = queryString.split("&");
        for (String param : params) {
            int index = param.indexOf("=");
            this.params.put(param.substring(0, index), param.substring(index + 1));
        }
    }
}
