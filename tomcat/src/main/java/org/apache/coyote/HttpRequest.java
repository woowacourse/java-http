package org.apache.coyote;

import java.util.Collections;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> param;
    private final String protocol;

    public HttpRequest(String method, String path, Map<String, String> param, String protocol) {
        this.method = method;
        this.path = path;
        this.param = Collections.unmodifiableMap(param);
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String name){
        return param.get(name);
    }

    public String getProtocol() {
        return protocol;
    }
}
