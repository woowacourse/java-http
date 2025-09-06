package org.apache.coyote.http.request;

import java.util.Collections;
import java.util.Map;

public class RequestPath {

    private final String path;
    private final Map<String, String> params;

    public RequestPath(String path, Map<String, String> params) {
        this.path = path;
        this.params = Collections.unmodifiableMap(params);
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public String getPath() {
        return path;
    }
}
