package org.apache.coyote.http11.request;

import java.util.List;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final String requestURI;

    public HttpRequest(List<String> request) {
        this.method = parseMethod(request);
        this.requestURI = parseRequestURI(request);
    }

    private String parseRequestURI(List<String> request) {
        String startLine = request.get(0);
        return startLine.split(" ")[1];
    }

    private HttpMethod parseMethod(List<String> request) {
        String startLine = request.get(0);
        return HttpMethod.of(startLine.split(" ")[0]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
