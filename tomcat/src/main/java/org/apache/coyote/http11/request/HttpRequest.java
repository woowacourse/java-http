package org.apache.coyote.http11.request;

import java.util.List;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final String requestURI;

    public HttpRequest(HttpMethod method, String url) {
        this.method = method;
        this.requestURI = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
