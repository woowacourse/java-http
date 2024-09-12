package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {
    private final Map<String, String> bodies;

    public HttpRequestBody(String line) {
        bodies = new HashMap<>();
        if(line.isBlank()) {
            return;
        }
        String[] params = line.split("&");
        for(String param: params) {
            int index = param.indexOf("=");
            bodies.put(param.substring(0, index), param.substring(index + 1));
        }
    }

    public Map<String, String> getBodies() {
        return bodies;
    }
}
