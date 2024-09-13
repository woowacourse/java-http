package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private static final String PARAMETER_SEPARATOR = "&";
    private static final String ASSIGN_OPERATOR = "=";

    private final Map<String, String> bodies;

    public HttpRequestBody(String line) {
        bodies = new HashMap<>();
        if(line.isBlank()) {
            return;
        }
        String[] params = line.split(PARAMETER_SEPARATOR);
        for(String param: params) {
            int index = param.indexOf(ASSIGN_OPERATOR);
            bodies.put(param.substring(0, index), param.substring(index + 1));
        }
    }

    public Map<String, String> getBodies() {
        return bodies;
    }
}
